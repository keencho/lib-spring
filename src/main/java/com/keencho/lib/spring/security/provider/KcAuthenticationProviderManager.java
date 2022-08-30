package com.keencho.lib.spring.security.provider;

import org.springframework.security.authentication.AuthenticationProvider;

import java.lang.reflect.Type;

/**
 * 인증 프로바이더 매니저
 * 이 매니저를 bean으로 등록한 후에 인증 프로바이더를 추가해야 한다.
 */
public interface KcAuthenticationProviderManager {

    AuthenticationProvider getAuthenticationProvider(Class<?> accountEntityClass);

}
