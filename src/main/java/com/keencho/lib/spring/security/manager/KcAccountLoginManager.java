package com.keencho.lib.spring.security.manager;

import com.keencho.lib.spring.security.model.KcAccountBaseModel;
import com.keencho.lib.spring.security.repository.KcAccountRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 로그인 관련 매니저
 *
 * @param <T> Account Entity
 * @param <R> Repository
 * @param <ID> Account Entity ID Type
 */
public interface KcAccountLoginManager<T extends KcAccountBaseModel, R extends KcAccountRepository<T, ID>, ID> {

    @Transactional(readOnly = true)
    T findByLoginId(String loginId);

    @Transactional
    void updateDtPasswordChangedAt(String loginId);

    @Transactional
    void updateDtLastAccessedAt(String loginId);

    @Transactional
    int updateLoginAttemptAccount(String loginId);

}
