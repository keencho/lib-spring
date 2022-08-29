package com.keencho.lib.spring.security.model;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class KcAccountBaseModel {

    public abstract int maxLoginAttemptCnt();

    public static int i = 1;

    protected String loginId;

    protected String password;

    protected int loginAttemptCnt = 0;

    protected boolean accountNonExpired;

    protected boolean accountNonLocked;

    protected boolean credentialsNonExpired;

    protected boolean enabled;

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

    public int getLoginAttemptCnt() {
        return loginAttemptCnt;
    }

    public void setLoginAttemptCnt(int loginAttemptCnt) {
        this.loginAttemptCnt = loginAttemptCnt;
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

    public LocalDateTime getDtLastAccessedAt() {
        return dtLastAccessedAt;
    }

    public void setDtLastAccessedAt(LocalDateTime dtLastAccessedAt) {
        this.dtLastAccessedAt = dtLastAccessedAt;
    }
}
