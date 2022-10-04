package com.keencho.lib.spring.jpa.querydsl.repository;

import com.keencho.lib.spring.jpa.querydsl.KcQueryHandler;
import com.querydsl.core.types.FactoryExpressionBase;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface KcSearchQuery<T> {

    List<T> findList(Predicate predicate);

    List<T> findList(Predicate predicate, KcQueryHandler queryHandler);

    List<T> findList(Predicate predicate, KcQueryHandler queryHandler, Sort sort);

    Page<T> findPage(Predicate predicate, Pageable pageable);

    Page<T> findPage(Predicate predicate, Pageable pageable, KcQueryHandler kcQueryHandler);

    <P> List<P> selectList(Predicate predicate, FactoryExpressionBase<P> factoryExpressionBase);

    <P> List<P> selectList(Predicate predicate, FactoryExpressionBase<P> factoryExpressionBase, KcQueryHandler queryHandler);

    <P> List<P> selectList(Predicate predicate, FactoryExpressionBase<P> factoryExpressionBase, KcQueryHandler queryHandler, Sort sort);

    <P> Page<P> selectPage(Predicate predicate, FactoryExpressionBase<P> factoryExpressionBase, Pageable pageable);

    <P> Page<P> selectPage(Predicate predicate, FactoryExpressionBase<P> factoryExpressionBase, Pageable pageable, KcQueryHandler kcQueryHandler);
}
