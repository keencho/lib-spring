package com.keencho.lib.spring.jpa.querydsl.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(ElementType.CONSTRUCTOR)
@Retention(RUNTIME)
public @interface KcQueryProjection {
}
