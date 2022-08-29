package com.keencho.lib.spring.security.model;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.util.Collection;

@MappedSuperclass
public abstract class KcAccountBaseModel {

    @Transient
    public abstract int maxLoginAttemptCnt();

    @Transient
    public abstract Collection<? extends GrantedAuthority> authorities();

    protected String loginId;

    protected String password;

    protected int loginAttemptCnt = 0;

    protected boolean accountNonExpired = true;

    protected boolean accountNonLocked = true;

    protected boolean credentialsNonExpired = true;

    protected boolean enabled = true;

    protected LocalDateTime dtCreatedAt = LocalDateTime.now();

    protected LocalDateTime dtUpdatedAt = LocalDateTime.now();

    protected LocalDateTime dtPasswordChangedAt = LocalDateTime.now();

    protected LocalDateTime dtLastAccessedAt;

    public KcAccountBaseModel() {
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setDtPasswordChangedAt(LocalDateTime dtPasswordChangedAt) {
        this.dtPasswordChangedAt = dtPasswordChangedAt;
    }

    public void setDtLastAccessedAt(LocalDateTime dtLastAccessedAt) {
        this.dtLastAccessedAt = dtLastAccessedAt;
    }
}
