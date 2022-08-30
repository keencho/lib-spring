package com.keencho.lib.spring.security.service;

import com.keencho.lib.spring.security.model.KcAccountBaseModel;
import com.keencho.lib.spring.security.repository.KcAccountRepository;

/**
 * 로그인 서비스
 * @param <T> Account Entity
 * @param <R> Account Repository
 * @param <ID> Account Entity Id
 * @param <D> Return Type
 */
public interface KcLoginService<T extends KcAccountBaseModel, R extends KcAccountRepository<T, ID>, ID , D> {
    Class<T> getAccountEntityClass();

    D login(String loginId, String password);
}
