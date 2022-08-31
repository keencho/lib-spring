package com.keencho.lib.spring.security.service;

import com.keencho.lib.spring.security.model.KcAccountBaseModel;
import com.keencho.lib.spring.security.repository.KcAccountRepository;

import javax.servlet.http.HttpServletResponse;

/**
 * 로그인 서비스
 *
 * @param <T> target entity
 * @param <R> target entity repository
 */
public interface KcLoginService<T extends KcAccountBaseModel, R extends KcAccountRepository<T, ?>> {
    Class<T> getAccountEntityClass();

    Object login(HttpServletResponse response, String loginId, String password);
}
