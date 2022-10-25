package com.keencho.lib.spring.jpa.querydsl.repository;

import com.keencho.lib.spring.jpa.querydsl.KcQBean;
import com.keencho.lib.spring.jpa.querydsl.KcQueryHandler;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

public class KcSearchQueryImpl<T> implements KcSearchQuery<T> {
    private final JPAQueryFactory queryFactory;
    private final EntityPath<T> path;
//    private final PathBuilder<?> pathBuilder;

    public KcSearchQueryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
        this.path = SimpleEntityPathResolver.INSTANCE.createPath(entityInformation.getJavaType());
//        this.pathBuilder = new PathBuilder<T>(path.getType(), path.getMetadata());
    }

    @Override
    public List<T> findList(Predicate predicate) {
        return this.findList(predicate, null, null);
    }

    @Override
    public List<T> findList(Predicate predicate, KcQueryHandler queryHandler) {
        return this.findList(predicate, queryHandler, null);
    }

    @Override
    public List<T> findList(Predicate predicate, KcQueryHandler queryHandler, Sort sort) {
        var q = this.createQuery();

        q = this.applyPredicate(q, predicate);
        q = this.applyQueryHandler(q, queryHandler);
        q = this.applySorting(q, sort);

        return q.select(path).fetch();
    }

    @Override
    public Page<T> findPage(Predicate predicate, Pageable pageable) {
        return this.findPage(predicate, pageable, null);
    }

    @Override
    public Page<T> findPage(Predicate predicate, Pageable pageable, KcQueryHandler kcQueryHandler) {

        Assert.notNull(pageable, "pageable must not be null!");

        var query = this.createQuery();

        query = this.applyPredicate(query, predicate);
        query = this.applyQueryHandler(query, kcQueryHandler);

        var totalSize = query.fetch().size();
        query = this.applyPagination(query, pageable);

        return new PageImpl<>(query.select(path).fetch(), pageable, totalSize);
    }

    @Override
    public <P> List<P> selectList(Predicate predicate, FactoryExpressionBase<P> factoryExpressionBase) {
        return this.selectList(predicate, factoryExpressionBase, null, null);
    }

    @Override
    public <P> List<P> selectList(Predicate predicate, FactoryExpressionBase<P> factoryExpressionBase, KcQueryHandler queryHandler) {
        return this.selectList(predicate, factoryExpressionBase, queryHandler, null);
    }

    @Override
    public <P> List<P> selectList(Predicate predicate, FactoryExpressionBase<P> factoryExpressionBase, KcQueryHandler queryHandler, Sort sort) {
        var q = this.createQuery();

        q = this.applyPredicate(q, predicate);
        q = this.applyQueryHandler(q, queryHandler);
        q = this.applySorting(q, sort);

        if (factoryExpressionBase instanceof KcQBean<P> kc) {
            if (!kc.isBuild) {
                factoryExpressionBase = kc.build();
            }
        }

        return q.select(factoryExpressionBase).fetch();
    }

    @Override
    public <P> Page<P> selectPage(Predicate predicate, FactoryExpressionBase<P> factoryExpressionBase, Pageable pageable) {
        return this.selectPage(predicate, factoryExpressionBase, pageable, null);
    }

    @Override
    public <P> Page<P> selectPage(Predicate predicate, FactoryExpressionBase<P> factoryExpressionBase, Pageable pageable, KcQueryHandler kcQueryHandler) {

        Assert.notNull(pageable, "pageable must not be null!");

        var query = this.createQuery();

        query = this.applyPredicate(query, predicate);
        query = this.applyQueryHandler(query, kcQueryHandler);

        var totalSize = query.fetch().size();
        query = this.applyPagination(query, pageable);

        if (factoryExpressionBase instanceof KcQBean<P> kc) {
            if (!kc.isBuild) {
                factoryExpressionBase = kc.build();
            }
        }

        return new PageImpl<>(query.select(factoryExpressionBase).fetch(), pageable, totalSize);
    }

    @Override
    public <P> List<P> selectList(Predicate predicate, Class<P> classType, Map<String, Expression<?>> bindings) {
        Assert.notNull(classType, "classType must not be null!");
        Assert.notEmpty(bindings, "bindings must not be empty!");

        var expression = Projections.bean(classType, bindings);

        return this.selectList(predicate, expression);
    }

    @Override
    public <P> List<P> selectList(Predicate predicate, Class<P> classType, Map<String, Expression<?>> bindings, KcQueryHandler queryHandler) {
        Assert.notNull(classType, "classType must not be null!");
        Assert.notEmpty(bindings, "bindings must not be empty!");

        var expression = Projections.bean(classType, bindings);

        return this.selectList(predicate, expression, queryHandler);
    }

    @Override
    public <P> List<P> selectList(Predicate predicate, Class<P> classType, Map<String, Expression<?>> bindings, KcQueryHandler queryHandler, Sort sort) {
        Assert.notNull(classType, "classType must not be null!");
        Assert.notEmpty(bindings, "bindings must not be empty!");

        var expression = Projections.bean(classType, bindings);

        return this.selectList(predicate, expression, queryHandler, sort);
    }

    @Override
    public <P> Page<P> selectPage(Predicate predicate, Class<P> classType, Map<String, Expression<?>> bindings, Pageable pageable) {
        Assert.notNull(classType, "classType must not be null!");
        Assert.notEmpty(bindings, "bindings must not be empty!");

        var expression = Projections.bean(classType, bindings);

        return this.selectPage(predicate, expression, pageable);
    }

    @Override
    public <P> Page<P> selectPage(Predicate predicate, Class<P> classType, Map<String, Expression<?>> bindings, Pageable pageable, KcQueryHandler kcQueryHandler) {
        Assert.notNull(classType, "classType must not be null!");
        Assert.notEmpty(bindings, "bindings must not be empty!");

        var expression = Projections.bean(classType, bindings);

        return this.selectPage(predicate, expression, pageable, kcQueryHandler);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    private JPAQuery<?> createQuery() {
        return queryFactory.from(path);
    }

    private JPAQuery<?> applyPredicate(JPAQuery<?> query, Predicate predicate) {
        if (predicate != null) {
            query = query.where(predicate);
        }

        return query;
    }

    private JPAQuery<?> applyQueryHandler(JPAQuery<?> query, KcQueryHandler queryHandler) {
        if (queryHandler != null) {
            query = queryHandler.apply(query);
        }

        return query;
    }

    private JPAQuery<?> applySorting(JPAQuery<?> query, Sort sort) {
        if (sort != null) {
            for (var order : sort) {
                var nullHandling = switch (order.getNullHandling()) {
                    case NULLS_FIRST:
                        yield  OrderSpecifier.NullHandling.NullsFirst;
                    case NULLS_LAST:
                        yield OrderSpecifier.NullHandling.NullsLast;
                    case NATIVE:
                        yield OrderSpecifier.NullHandling.Default;
                };

                query = query.orderBy(new OrderSpecifier<>(order.isAscending() ? Order.ASC : Order.DESC, Expressions.stringPath(order.getProperty()), nullHandling));
            }
        }

        return query;
    }

    private JPAQuery<?> applyPagination(JPAQuery<?> query, Pageable pageable) {
        if (pageable != null) {
            query = query.offset(pageable.getOffset()).limit(pageable.getPageSize());

            return this.applySorting(query, pageable.getSort());
        }

        return query;
    }
}
