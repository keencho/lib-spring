package com.keencho.lib.spring.jpa.querydsl;

import com.querydsl.jpa.impl.JPAQuery;

@FunctionalInterface
public interface KcQueryHandler {
    JPAQuery<?> apply(JPAQuery<?> query);
}
