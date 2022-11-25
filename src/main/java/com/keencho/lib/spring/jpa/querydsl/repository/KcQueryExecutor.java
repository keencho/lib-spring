package com.keencho.lib.spring.jpa.querydsl.repository;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAUpdateClause;

import java.util.Map;

public interface KcQueryExecutor<T> extends KcSearchQuery<T> {

    JPAUpdateClause createUpdateClause();

    long updateOne(Predicate predicate, Map<Path<?>, ?> data);

}
