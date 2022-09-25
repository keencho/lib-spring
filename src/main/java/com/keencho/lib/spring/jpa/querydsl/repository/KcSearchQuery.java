package com.keencho.lib.spring.jpa.querydsl.repository;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Predicate;
import org.springframework.data.querydsl.QSort;

import java.util.List;

public interface KcSearchQuery<T> {
    List<T> findList(Predicate predicate);

    List<T> findList(Predicate predicate, QSort sort);

    <P> List<P> selectList(Predicate predicate, Class<? extends ConstructorExpression<P>> kcQueryProjectionClass);
}
