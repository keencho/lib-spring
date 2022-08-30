package com.keencho.lib.spring.security.manager;

import com.keencho.lib.spring.security.model.KcAccountBaseModel;
import com.keencho.lib.spring.security.repository.KcAccountRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * 로그인 관련 매니저
 *
 * @param <T> Account Entity
 * @param <R> Repository
 * @param <ID> Account Entity ID Type
 */
public interface KcLoginManager<T extends KcAccountBaseModel, R extends KcAccountRepository<T, ID>, ID> extends UserDetailsService {

    Collection<? extends GrantedAuthority> getAuthorities();

    int getMaxLoginAttemptCount();

    int getMaxLongTermNonUseAllowDay();

    @Transactional(readOnly = true)
    T findByLoginId(String loginId);

    @Transactional
    void updateOnLoginSuccess(String loginId);

    @Transactional
    int updateLoginAttemptAccount(String loginId);

    @Transactional
    void lockAccount(String loginId);

}
