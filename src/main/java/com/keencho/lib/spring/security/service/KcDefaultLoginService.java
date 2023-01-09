package com.keencho.lib.spring.security.service;

import com.keencho.lib.spring.common.exception.KcSystemException;
import com.keencho.lib.spring.security.exception.KcAccountDisabledException;
import com.keencho.lib.spring.security.exception.KcAccountLockedException;
import com.keencho.lib.spring.security.exception.KcAccountLongTermNotUsedException;
import com.keencho.lib.spring.security.exception.KcLoginFailureException;
import com.keencho.lib.spring.security.manager.KcLoginManager;
import com.keencho.lib.spring.security.model.KcAccountBaseModel;
import com.keencho.lib.spring.security.model.KcSecurityAccount;
import com.keencho.lib.spring.security.provider.KcJwtTokenProvider;
import com.keencho.lib.spring.security.provider.manager.KcAuthenticationProviderManager;
import com.keencho.lib.spring.security.repository.KcAccountRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

public abstract class KcDefaultLoginService<T extends KcAccountBaseModel, R extends KcAccountRepository<T, ?>> implements KcLoginService<T, R> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final KcAuthenticationProviderManager authenticationProviderManager;
    private final KcLoginManager<T, R> accountLoginManager;
    private final KcJwtTokenProvider jwtTokenProvider;
    private boolean isUseJwtToken = false;

    public KcDefaultLoginService(KcAuthenticationProviderManager authenticationProviderManager, KcLoginManager<T, R> accountLoginManager, KcJwtTokenProvider jwtTokenProvider) {
        this.authenticationProviderManager = authenticationProviderManager;
        this.accountLoginManager = accountLoginManager;
        this.jwtTokenProvider = jwtTokenProvider;

        // jwtTokenProvider가 주입되었다면 jwt token 방식을 사용하는 것이라고 간주한다.
        if (this.jwtTokenProvider != null) {
            this.isUseJwtToken = true;
        }
    }

    public abstract Class<T> getAccountEntityClass();

    @Override
    public Object login(HttpServletResponse response, String loginId, String password) {
        Authentication authentication;
        var token = new UsernamePasswordAuthenticationToken(loginId, password);

        try {
            var authenticationProvider = authenticationProviderManager.getAuthenticationProvider(getAccountEntityClass());

            // 코딩 잘못임. bean에 엔티티 클래스 / 매니저 등록 안함.
            if (authenticationProvider == null) {
                logger.error("system error: authentication provider manager doesn't have target entity class. check your bean configuration");
                throw new KcSystemException();
            }

            authentication = authenticationProvider.authenticate(token);

            // 여기까지 들어왔으면 아이디 / 비밀번호는 일치한다는 의미임
            // 계정을 잠금처리하는 batch 따로 돌아야 하겠지만 돌기 전에 이곳에 들어왔다고 가정하고 장기 미접속 계정 잠금처리
            if (this.isLongTermNotUsed(loginId)) {
                accountLoginManager.lockAccount(loginId);
                throw new LockedException("account locked");
            }

            // jwt 인증은 별도로 구현한 인증과정을 거쳐야 한다는 뜻.
            if (!this.isUseJwtToken) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (BadCredentialsException ex) {
            var cnt = accountLoginManager.updateLoginAttemptAccount(loginId);

            // 계정 없음
            if (cnt == -1) {
                throw new KcLoginFailureException();
            } else {
                throw new KcLoginFailureException(cnt, accountLoginManager.getMaxLoginAttemptCount());
            }

        } catch (LockedException ex) {
            // 계정이 잠긴 이유는 일단 2가지라고 생각함.
            // 1. N회 비밀번호 틀린 경우
            // 2. 장기 미접속
            // 따라서 장기 미접속 여부 판단해야함.
            if (isLongTermNotUsed(loginId)) {
                throw new KcAccountLongTermNotUsedException();
            }

            throw new KcAccountLockedException();
        } catch (DisabledException ex) {
            throw new KcAccountDisabledException();
        } catch (KcSystemException ex) {
            throw new KcSystemException();
        } catch (Exception ex) {
            throw new KcLoginFailureException("login failure");
        }

        accountLoginManager.updateOnLoginSuccess(token.getPrincipal().toString());

        var securityUser = (KcSecurityAccount) authentication.getPrincipal();

        if (this.isUseJwtToken) {
            var jwtToken = this.jwtTokenProvider.createToken(securityUser);

            // 토큰을 쿠키에 저장한다. 이때 이름은 tokenProvider에 정의해둔 이름을 사용한다.
            // 필터가 그 이름을 알수 있어야 하는데, 여기서는 KcDefaultJwtTokenProvider에도 동일한 jwtTokenProvider를 주입받게 해두었다.
            // 구현하는 사람 맘대로 하면 된다. 어쨌든 이 쿠키 이름을 필터가 알아야 한다.
            var cookie = ResponseCookie.from(this.jwtTokenProvider.getCookieName(), jwtToken)
                    .sameSite("None")
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(this.jwtTokenProvider.getExpireDays() * 86400)
                    .build();

            response.addHeader("Set-Cookie", cookie.toString());

            return jwtToken;
        }

        return securityUser.getData();
    }

    /**
     * 장기 미접속여부 판단
     * 
     * @param loginId
     * @return true if not use for long time
     */
    private boolean isLongTermNotUsed(String loginId) {
        var account = accountLoginManager.findByLoginId(loginId);
        var maxLongTermNonUseAllowDay = accountLoginManager.getMaxLongTermNonUseAllowDay();
        
        if (account != null) {
            if (maxLongTermNonUseAllowDay > 0) {
                if (account.getDtLastAccessedAt() != null) {
                    if (account.getDtLastAccessedAt().plusDays(maxLongTermNonUseAllowDay).isBefore(LocalDateTime.now())) {
                        return true;
                    }
                }
            }   
        }
        
        return false;
    }
}
