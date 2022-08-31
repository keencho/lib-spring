package com.keencho.lib.spring.security.resolver.manager;

import com.keencho.lib.spring.security.resolver.KcAccountResolver;
import io.jsonwebtoken.lang.Assert;

import java.util.HashMap;
import java.util.Map;

public class KcAccountResolverManagerImpl implements KcAccountResolverManager{

    private final Map<Class<?>, KcAccountResolver<?>> kcAccountResolverMap = new HashMap<>();

    public KcAccountResolverManagerImpl() {
    }

    @Override
    public KcAccountResolver<?> getKcAccountResolver(Class<?> accountEntityClass) {
        return this.kcAccountResolverMap.get(accountEntityClass);
    }

    public void addAccountResolver(Class<?> accountEntityClass, KcAccountResolver<?> resolver) {
        Assert.isTrue(!this.kcAccountResolverMap.containsKey(accountEntityClass), "kcAccountResolverMap already has key");

        this.kcAccountResolverMap.put(accountEntityClass, resolver);
    }
}
