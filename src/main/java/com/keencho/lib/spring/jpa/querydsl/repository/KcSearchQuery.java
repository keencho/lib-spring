package com.keencho.lib.spring.jpa.querydsl.repository;

import com.querydsl.core.types.FactoryExpressionBase;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface KcSearchQuery<T> {
    List<T> findList(Predicate predicate, Sort sort);

    <P> List<P> selectList(Predicate predicate, FactoryExpressionBase<P> factoryExpressionBase, Sort sort);
}
