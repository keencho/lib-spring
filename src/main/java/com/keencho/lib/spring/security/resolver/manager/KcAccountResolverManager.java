package com.keencho.lib.spring.security.resolver.manager;

import com.keencho.lib.spring.security.resolver.KcAccountResolver;

public interface KcAccountResolverManager {
    KcAccountResolver<?> getKcAccountResolver(Class<?> accountEntityClass);
}
