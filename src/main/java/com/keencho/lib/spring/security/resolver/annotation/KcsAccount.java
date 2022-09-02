package com.keencho.lib.spring.security.resolver.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface KcsAccount {
    KcsAccountType accountType() default KcsAccountType.ACCOUNT_ENTITY;
    boolean required() default false;
}
