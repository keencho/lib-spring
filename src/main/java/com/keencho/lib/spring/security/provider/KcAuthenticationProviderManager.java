package com.keencho.lib.spring.security.provider;

import java.util.Collection;

/**
 * 인증 프로바이더 매니저
 * 이 매니저를 bean으로 등록한 후에 인증 프로바이더를 추가해야 한다.
 */
public interface KcAuthenticationProviderManager {

    Collection<KcAuthenticationProvider> getProviders();

    KcAuthenticationProvider getAuthenticationProvider(Class<?> accountEntityClass);

}
