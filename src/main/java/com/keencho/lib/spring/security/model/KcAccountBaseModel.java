package com.keencho.lib.spring.security.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class KcAccountBaseModel {

    @Column(nullable = false)
    protected String loginId;

    @Column(nullable = false)
    protected String password;

    @Column(nullable = false)
    protected boolean accountNonExpired = true;

    @Column(nullable = false)
    protected boolean accountNonLocked = true;

    @Column(nullable = false)
    protected boolean credentialsNonExpired = true;

    @Column(nullable = false)
    protected boolean enabled = true;

    @Column(nullable = false)
    protected LocalDateTime dtCreatedAt = LocalDateTime.now();

    @Column(nullable = false)
    protected LocalDateTime dtUpdatedAt = LocalDateTime.now();

    protected LocalDateTime dtPasswordChangedAt = LocalDateTime.now();

    protected LocalDateTime dtLastLoggedInAt;

    protected LocalDateTime dtLastAccessedAt;

    @Column(nullable = false)
    protected int loginAttemptCount = 0;

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

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getDtCreatedAt() {
        return dtCreatedAt;
    }

    public void setDtCreatedAt(LocalDateTime dtCreatedAt) {
        this.dtCreatedAt = dtCreatedAt;
    }

    public LocalDateTime getDtUpdatedAt() {
        return dtUpdatedAt;
    }

    public void setDtUpdatedAt(LocalDateTime dtUpdatedAt) {
        this.dtUpdatedAt = dtUpdatedAt;
    }

    public LocalDateTime getDtPasswordChangedAt() {
        return dtPasswordChangedAt;
    }

    public void setDtPasswordChangedAt(LocalDateTime dtPasswordChangedAt) {
        this.dtPasswordChangedAt = dtPasswordChangedAt;
    }

    public LocalDateTime getDtLastLoggedInAt() {
        return dtLastLoggedInAt;
    }

    public void setDtLastLoggedInAt(LocalDateTime dtLastLoggedInAt) {
        this.dtLastLoggedInAt = dtLastLoggedInAt;
    }

    public LocalDateTime getDtLastAccessedAt() {
        return dtLastAccessedAt;
    }

    public void setDtLastAccessedAt(LocalDateTime dtLastAccessedAt) {
        this.dtLastAccessedAt = dtLastAccessedAt;
    }

    public int getLoginAttemptCount() {
        return loginAttemptCount;
    }

    public void setLoginAttemptCount(int loginAttemptCount) {
        this.loginAttemptCount = loginAttemptCount;
    }
}
