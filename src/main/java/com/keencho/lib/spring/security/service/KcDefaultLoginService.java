package com.keencho.lib.spring.security.service;

import com.keencho.lib.spring.security.exception.KcLoginFailureException;
import com.keencho.lib.spring.security.manager.KcAccountLoginManager;
import com.keencho.lib.spring.security.model.KcAccountBaseModel;
import com.keencho.lib.spring.security.model.KcSecurityAccount;
import com.keencho.lib.spring.security.provider.KcAuthenticationProviderManager;
import com.keencho.lib.spring.security.repository.KcAccountRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class KcDefaultLoginService<T extends KcAccountBaseModel, R extends KcAccountRepository<T, ID>, ID, D> implements KcLoginService<T, R, ID, D> {

    private final KcAuthenticationProviderManager authenticationProviderManager;
    private final KcAccountLoginManager<T, R, ID> accountLoginManager;

    public KcDefaultLoginService(KcAuthenticationProviderManager authenticationProviderManager, KcAccountLoginManager<T, R, ID> accountLoginManager) {
        this.authenticationProviderManager = authenticationProviderManager;
        this.accountLoginManager = accountLoginManager;
    }

    public abstract Class<T> getAccountEntityClass();

    @Override
    public D login(String loginId, String password) {
        Authentication authentication;
        var token = new UsernamePasswordAuthenticationToken(loginId, password);

        try {
            var authenticationProvider = authenticationProviderManager.getAuthenticationProvider(getAccountEntityClass());
            authentication = authenticationProvider.authenticate(token);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (BadCredentialsException ex) {
            var cnt = accountLoginManager.updateLoginAttemptAccount(loginId);

            // 계정 없음
            if (cnt == 0) {
                throw new KcLoginFailureException();
            } else {
                var max = accountLoginManager.getMaxLoginAttemptCount();
                throw new KcLoginFailureException();
            }
        } catch (LockedException ex) {
            throw ex;
        } catch (DisabledException ex) {
            throw ex;
        } catch (Exception ex) {
            throw ex;
        }

        accountLoginManager.updateDtLastAccessedAt(token.getPrincipal().toString());

        var securityUser = (KcSecurityAccount<D>) authentication.getPrincipal();

        return securityUser.getData();
    }
}
