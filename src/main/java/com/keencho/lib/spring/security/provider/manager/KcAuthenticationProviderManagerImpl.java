package com.keencho.lib.spring.security.provider.manager;

import com.keencho.lib.spring.security.provider.KcAuthenticationProvider;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class KcAuthenticationProviderManagerImpl implements KcAuthenticationProviderManager {

    private final Map<Class<?>, KcAuthenticationProvider> authenticationProviderMap = new HashMap<>();

    @Override
    public Collection<KcAuthenticationProvider> getProviders() {
        return this.authenticationProviderMap.values();
    }

    @Override
    public KcAuthenticationProvider getAuthenticationProvider(Class<?> accountEntityClass) {
        return this.authenticationProviderMap.get(accountEntityClass);
    }

    public void addAuthenticationProvider(Class<?> accountEntityClass, KcAuthenticationProvider authenticationProvider) {
        Assert.isTrue(!this.authenticationProviderMap.containsKey(accountEntityClass), "authenticationProviderMap already has key");

        this.authenticationProviderMap.put(accountEntityClass, authenticationProvider);
    }
}
