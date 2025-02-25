package com.keencho.lib.spring.jpa.querydsl.repository;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAUpdateClause;

import java.util.Map;
import java.util.function.Function;

public interface KcQueryExecutor<T> extends KcSearchQuery<T> {

    JPAUpdateClause createUpdateClause();

    long updateOne(Predicate predicate, Map<Path<?>, ?> data);

    long update(Predicate predicate, Map<Path<?>, ?> data);

    long update(Function<JPAUpdateClause, Long> update);

    JPADeleteClause createDeleteClause();

    long deleteOne(Predicate predicate);

    long delete(Predicate predicate);

}
