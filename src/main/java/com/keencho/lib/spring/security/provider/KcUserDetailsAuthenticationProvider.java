package com.keencho.lib.spring.security.provider;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class KcUserDetailsAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    public KcUserDetailsAuthenticationProvider(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }

        var passedPassword = authentication.getCredentials().toString();

        if (!passwordEncoder.matches(passedPassword, userDetails.getPassword())) {
            throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        UserDetails loadedUser;

        try {
            loadedUser = this.userDetailsService.loadUserByUsername(authentication.getPrincipal().toString());
        } catch (UsernameNotFoundException notFoundException) {
            throw notFoundException;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }

        if (loadedUser == null) {
            throw new InternalAuthenticationServiceException("DefaultUserDetailsService returned null, it must be not null!");
        }

        return loadedUser;
    }
}
