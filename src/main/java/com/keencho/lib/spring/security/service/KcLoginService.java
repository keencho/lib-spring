package com.keencho.lib.spring.security.service;

import com.keencho.lib.spring.security.model.KcAccountBaseModel;
import com.keencho.lib.spring.security.repository.KcAccountRepository;

import javax.servlet.http.HttpServletResponse;

/**
 * 로그인 서비스
 * @param <T> Account Entity
 * @param <R> Account Repository
 * @param <ID> Account Entity Id
 */
public interface KcLoginService<T extends KcAccountBaseModel, R extends KcAccountRepository<T, ID>, ID> {
    Class<T> getAccountEntityClass();

    Object login(HttpServletResponse response, String loginId, String password);
}
