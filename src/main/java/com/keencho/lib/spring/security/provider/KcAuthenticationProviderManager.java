package com.keencho.lib.spring.security.provider;

import org.springframework.security.authentication.AuthenticationProvider;

import java.lang.reflect.Type;

public interface KcAuthenticationProviderManager {

    AuthenticationProvider getAuthenticationProvider(Class<?> accountEntityClass);

}
