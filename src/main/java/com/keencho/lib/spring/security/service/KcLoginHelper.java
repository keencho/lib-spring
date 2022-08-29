package com.keencho.lib.spring.security.service;

import com.keencho.lib.spring.security.model.KcAccountBaseModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

public interface KcLoginHelper<ACCOUNT extends KcAccountBaseModel, LOGIN_DATA> extends UserDetailsService {

    @Transactional(readOnly = true)
    ACCOUNT findByLoginId(String loginId);

    Collection<? extends GrantedAuthority> getAuthorities();

    @Transactional
    void updateDtPasswordChangedAt(String loginId);

    @Transactional
    void updateDtLastAccessedAt(String loginId);

    LOGIN_DATA login(String loginId, String password);
}
