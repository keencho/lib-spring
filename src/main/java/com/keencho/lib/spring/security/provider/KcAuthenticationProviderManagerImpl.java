package com.keencho.lib.spring.security.provider;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.util.Assert;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class KcAuthenticationProviderManagerImpl implements KcAuthenticationProviderManager{

    private final Map<Class<?>, AuthenticationProvider> authenticationProviderMap = new HashMap<>();

    @Override
    public AuthenticationProvider getAuthenticationProvider(Class<?> accountEntityClass) {
        return this.authenticationProviderMap.get(accountEntityClass);
    }

    public void addAuthenticationProvider(Class<?> accountEntityClass, AuthenticationProvider authenticationProvider) {
        Assert.isTrue(!this.authenticationProviderMap.containsKey(accountEntityClass), "authenticationProviderMap already has key");

        this.authenticationProviderMap.put(accountEntityClass, authenticationProvider);
    }
}
