package com.keencho.lib.spring.security.service;

import com.keencho.lib.spring.common.exception.KcSystemException;
import com.keencho.lib.spring.security.exception.KcAccountDisabledException;
import com.keencho.lib.spring.security.exception.KcAccountLockedException;
import com.keencho.lib.spring.security.exception.KcAccountLongTermNotUsedException;
import com.keencho.lib.spring.security.exception.KcLoginFailureException;
import com.keencho.lib.spring.security.manager.KcLoginManager;
import com.keencho.lib.spring.security.model.KcAccountBaseModel;
import com.keencho.lib.spring.security.model.KcAuthReturnType;
import com.keencho.lib.spring.security.model.KcSecurityAccount;
import com.keencho.lib.spring.security.provider.KcAuthenticationProviderManager;
import com.keencho.lib.spring.security.provider.KcJwtTokenProvider;
import com.keencho.lib.spring.security.repository.KcAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

public abstract class KcDefaultLoginService<T extends KcAccountBaseModel, R extends KcAccountRepository<T, ID>, ID, D> implements KcLoginService<T, R, ID, D> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final KcAuthenticationProviderManager authenticationProviderManager;
    private final KcLoginManager<T, R, ID> accountLoginManager;
    private final KcJwtTokenProvider jwtTokenProvider;

    public KcDefaultLoginService(KcAuthenticationProviderManager authenticationProviderManager, KcLoginManager<T, R, ID> accountLoginManager, KcJwtTokenProvider jwtTokenProvider) {
        this.authenticationProviderManager = authenticationProviderManager;
        this.accountLoginManager = accountLoginManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public abstract Class<T> getAccountEntityClass();
    public abstract KcAuthReturnType getAuthReturnType();

    @Override
    public D login(String loginId, String password) {
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

            if (this.getAuthReturnType() == KcAuthReturnType.AUTHENTICATION) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (BadCredentialsException ex) {
            var cnt = accountLoginManager.updateLoginAttemptAccount(loginId);

            // 계정 없음
            if (cnt == 0) {
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

        var securityUser = (KcSecurityAccount<D>) authentication.getPrincipal();

        // 코딩 잘못임. jwt token을 리턴해야 하는데 jwt token provider가 주입되지 않음.\
        if (this.getAuthReturnType() == KcAuthReturnType.JWT_TOKEN) {
            if (this.jwtTokenProvider == null) {
                logger.error("system error: jwt token provider is null");
                throw new KcSystemException();
            }
            return (D) this.jwtTokenProvider.createToken(securityUser);
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
