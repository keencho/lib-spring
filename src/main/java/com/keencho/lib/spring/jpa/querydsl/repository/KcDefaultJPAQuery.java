package com.keencho.lib.spring.jpa.querydsl.repository;

import com.keencho.lib.spring.jpa.querydsl.KcQBean;
import com.keencho.lib.spring.jpa.querydsl.KcQueryHandler;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KcDefaultJPAQuery<T> implements KcQueryExecutor<T> {
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;
    private final EntityPath<T> path;

    public KcDefaultJPAQuery(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
        this.path = SimpleEntityPathResolver.INSTANCE.createPath(entityInformation.getJavaType());
//        this.pathBuilder = new PathBuilder<T>(path.getType(), path.getMetadata());
    }

    @Override
    public T findOne(Predicate predicate) {
        return this.findOne(predicate, null);
    }

    @Override
    public T findOne(Predicate predicate, KcQueryHandler queryHandler) {
        var q = this.createQuery();

        q = this.applyPredicate(q, predicate);
        q = this.applyQueryHandler(q, queryHandler);

        return q.select(path).fetchOne();
    }

    @Override
    public <P> P selectOne(Predicate predicate, KcQBean<P> qBean) {
        return this.selectOne(predicate, qBean, null);
    }

    @Override
    public <P> P selectOne(Predicate predicate, KcQBean<P> qBean, KcQueryHandler queryHandler) {
        var q = this.createQuery();

        q = this.applyPredicate(q, predicate);
        q = this.applyQueryHandler(q, queryHandler);

        if (!qBean.isBuild) {
            qBean = qBean.build();
        }

        return q.select(qBean).fetchOne();
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
    public <P> List<P> selectList(Predicate predicate, KcQBean<P> qBean) {
        return this.selectList(predicate, qBean, null, null);
    }

    @Override
    public <P> List<P> selectList(Predicate predicate, KcQBean<P> qBean, KcQueryHandler queryHandler) {
        return this.selectList(predicate, qBean, queryHandler, null);
    }

    @Override
    public <P> List<P> selectList(Predicate predicate, KcQBean<P> qBean, KcQueryHandler queryHandler, Sort sort) {
        var q = this.createQuery();

        q = this.applyPredicate(q, predicate);
        q = this.applyQueryHandler(q, queryHandler);

        if (!qBean.isBuild) {
            qBean = qBean.build();
        }
        q = this.applySorting(q, sort);

        return q.select(qBean).fetch();
    }

    @Override
    public <P> Page<P> selectPage(Predicate predicate, KcQBean<P> qBean, Pageable pageable) {
        return this.selectPage(predicate, qBean, pageable, null);
    }

    @Override
    public <P> Page<P> selectPage(Predicate predicate, KcQBean<P> qBean, Pageable pageable, KcQueryHandler kcQueryHandler) {

        Assert.notNull(pageable, "pageable must not be null!");

        var query = this.createQuery();

        query = this.applyPredicate(query, predicate);
        query = this.applyQueryHandler(query, kcQueryHandler);

        var totalSize = query.fetch().size();

        if (!qBean.isBuild) {
            qBean = qBean.build();
        }

        query = this.applyPagination(query, pageable);

        return new PageImpl<>(query.select(qBean).fetch(), pageable, totalSize);
    }

    @Override
    public <P> List<P> selectList(Predicate predicate, Class<P> classType, Map<String, Expression<?>> bindings) {
        Assert.notNull(classType, "classType must not be null!");
        Assert.notEmpty(bindings, "bindings must not be empty!");

        var expression = new KcQBean<>(classType, bindings);

        return this.selectList(predicate, expression);
    }

    @Override
    public <P> List<P> selectList(Predicate predicate, Class<P> classType, Map<String, Expression<?>> bindings, KcQueryHandler queryHandler) {
        Assert.notNull(classType, "classType must not be null!");
        Assert.notEmpty(bindings, "bindings must not be empty!");

        var expression = new KcQBean<>(classType, bindings);

        return this.selectList(predicate, expression, queryHandler);
    }

    @Override
    public <P> List<P> selectList(Predicate predicate, Class<P> classType, Map<String, Expression<?>> bindings, KcQueryHandler queryHandler, Sort sort) {
        Assert.notNull(classType, "classType must not be null!");
        Assert.notEmpty(bindings, "bindings must not be empty!");

        var expression = new KcQBean<>(classType, bindings);

        return this.selectList(predicate, expression, queryHandler, sort);
    }

    @Override
    public <P> Page<P> selectPage(Predicate predicate, Class<P> classType, Map<String, Expression<?>> bindings, Pageable pageable) {
        Assert.notNull(classType, "classType must not be null!");
        Assert.notEmpty(bindings, "bindings must not be empty!");

        var expression = new KcQBean<>(classType, bindings);

        return this.selectPage(predicate, expression, pageable);
    }

    @Override
    public <P> Page<P> selectPage(Predicate predicate, Class<P> classType, Map<String, Expression<?>> bindings, Pageable pageable, KcQueryHandler kcQueryHandler) {
        Assert.notNull(classType, "classType must not be null!");
        Assert.notEmpty(bindings, "bindings must not be empty!");

        var expression = new KcQBean<>(classType, bindings);

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

    // update clause
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public JPAUpdateClause createUpdateClause() {
        return new JPAUpdateClause(entityManager, path);
    }

    @Override
    public long updateOne(Predicate predicate, Map<Path<?>, ?> data) {
        Assert.notNull(predicate, "predicate must not be null");
        Assert.notEmpty(data, "update path data must not be empty!");

        var paths = new ArrayList<Path<?>>();
        var values = new ArrayList<>();

        for (var e : data.entrySet()) {
            paths.add(e.getKey());
            values.add(e.getValue());
        }

        var result = this.createUpdateClause()
                .where(predicate)
                .set(paths, values)
                .execute();

        Assert.isTrue(result == 1, "update result is not 1");

        return result;
    }
}
