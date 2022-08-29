package com.keencho.lib.spring.security.service;

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

public abstract class KcLoginServiceImpl<ACCOUNT extends KcAccountBaseModel, REPO extends KcAccountRepository<ACCOUNT, KEY>, KEY, LOGIN_DATA> implements KcLoginService<ACCOUNT, REPO, KEY, LOGIN_DATA> {

    private final KcAuthenticationProviderManager authenticationProviderManager;
    private final KcAccountLoginManager<ACCOUNT, REPO, KEY> accountLoginManager;

    public KcLoginServiceImpl(KcAuthenticationProviderManager authenticationProviderManager, KcAccountLoginManager<ACCOUNT, REPO, KEY> accountLoginManager) {
        this.authenticationProviderManager = authenticationProviderManager;
        this.accountLoginManager = accountLoginManager;
    }

    protected abstract Class<?> getAccountEntityClass();

    @Override
    public LOGIN_DATA login(String loginId, String password) {
        Authentication authentication = null;
        var token = new UsernamePasswordAuthenticationToken(loginId, password);

        try {
            var authenticationProvider = authenticationProviderManager.getAuthenticationProvider(this.getAccountEntityClass());
            authentication = authenticationProvider.authenticate(token);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (BadCredentialsException ex) {

        } catch (LockedException ex) {

        } catch (DisabledException ex) {

        } catch (Exception ex) {

        }

        accountLoginManager.updateDtLastAccessedAt(token.getPrincipal().toString());

        var securityUser = (KcSecurityAccount<LOGIN_DATA>) authentication.getPrincipal();

        return securityUser.getData();
    }
}
