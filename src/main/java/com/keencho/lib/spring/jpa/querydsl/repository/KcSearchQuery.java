package com.keencho.lib.spring.jpa.querydsl.repository;

import com.keencho.lib.spring.jpa.querydsl.KcQBean;
import com.keencho.lib.spring.jpa.querydsl.KcQueryHandler;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

public interface KcSearchQuery<T> {

    T findOne(Predicate predicate);

    T findOne(Predicate predicate, KcQueryHandler queryHandler);

    <P> P selectOne(Predicate predicate, KcQBean<P> qBean);

    <P> P selectOne(Predicate predicate, KcQBean<P> qBean, KcQueryHandler queryHandler);

    /////////////////////////////////////////////////////// find pure entity

    List<T> findList(Predicate predicate);

    List<T> findList(Predicate predicate, KcQueryHandler queryHandler);

    List<T> findList(Predicate predicate, KcQueryHandler queryHandler, Sort sort);

    Page<T> findPage(Predicate predicate, Pageable pageable);

    Page<T> findPage(Predicate predicate, Pageable pageable, KcQueryHandler kcQueryHandler);

    /////////////////////////////////////////////////////// select projection by KcQBean class

    <P> List<P> selectList(Predicate predicate, KcQBean<P> qBean);

    <P> List<P> selectList(Predicate predicate, KcQBean<P> qBean, KcQueryHandler queryHandler);

    <P> List<P> selectList(Predicate predicate, KcQBean<P> qBean, KcQueryHandler queryHandler, Sort sort);

    <P> Page<P> selectPage(Predicate predicate, KcQBean<P> qBean, Pageable pageable);

    <P> Page<P> selectPage(Predicate predicate, KcQBean<P> qBean, Pageable pageable, KcQueryHandler kcQueryHandler);

    /////////////////////////////////////////////////////// select projection by map binding and convert it to target class

    <P> List<P> selectList(Predicate predicate, Class<P> classType, Map<String, Expression<?>> bindings);

    <P> List<P> selectList(Predicate predicate, Class<P> classType, Map<String, Expression<?>> bindings, KcQueryHandler queryHandler);

    <P> List<P> selectList(Predicate predicate, Class<P> classType, Map<String, Expression<?>> bindings, KcQueryHandler queryHandler, Sort sort);

    <P> Page<P> selectPage(Predicate predicate, Class<P> classType, Map<String, Expression<?>> bindings, Pageable pageable);

    <P> Page<P> selectPage(Predicate predicate, Class<P> classType, Map<String, Expression<?>> bindings, Pageable pageable, KcQueryHandler kcQueryHandler);
}
