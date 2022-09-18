package com.keencho.lib.spring.security.model;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

public class KcSecurityAccount implements UserDetails, CredentialsContainer {

    private final Class<?> accountEntityClass;
    private final String loginId;
    private String password;

    private final Set<GrantedAuthority> authorities;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;
    private final Object data;

    public KcSecurityAccount(Class<?> accountEntityClass, String loginId, String password, Set<GrantedAuthority> authorities,
                             boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired, boolean enabled,
                             Object data) {
        this.accountEntityClass = accountEntityClass;
        this.loginId = loginId;
        this.password = password;
        this.authorities = authorities;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
        this.data = data;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    public Class<?> getAccountEntityClass() {
        return accountEntityClass;
    }

    public String getLoginId() {
        return loginId;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return null;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Set<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public Object getData() {
        return data;
    }
}
