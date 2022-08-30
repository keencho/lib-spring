package com.keencho.lib.spring.security.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)
@Data
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
}
