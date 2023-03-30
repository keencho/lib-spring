package com.keencho.lib.spring.jpa.querydsl.repository;

import com.keencho.lib.spring.jpa.querydsl.KcExpression;
import com.keencho.lib.spring.jpa.querydsl.KcQueryHandler;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.querydsl.QSort;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KcDefaultJPAQuery<T> implements KcQueryExecutor<T> {
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;
    private final EntityPath<T> path;

    // querydsl 에 의해 성생된 Q class 가 존재하는 경우 - path로 Q 클래스를 사용하도록 함 
    public KcDefaultJPAQuery(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
        this.path = SimpleEntityPathResolver.INSTANCE.createPath(entityInformation.getJavaType());
    }

    // 생성된 Q class 가 없거나 이를 사용할 수 없는 경우 (unit test) - 새로운 EntityPath를 생성함
    public KcDefaultJPAQuery(Class<T> clazz, EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
        this.path = new EntityPathBase<>(clazz, "entity");
    }

    @Override
    public Map<String, Object> selectOne(Predicate predicate, Map<String, Expression<?>> bindings) {
        return this.selectOne(predicate, bindings, null);
    }

    @Override
    public Map<String, Object> selectOne(Predicate predicate, Map<String, Expression<?>> bindings, KcQueryHandler handler) {
        return this.selectOne(predicate, bindings, handler, null);
    }

    @Override
    public Map<String, Object> selectOne(Predicate predicate, Map<String, Expression<?>> bindings, KcQueryHandler handler, Sort sort) {
        Assert.notNull(bindings, "bindings must not be null!");
        Assert.notEmpty(bindings, "bindings must not be empty!");

        var q = this.createQuery();

        q = this.applyPredicate(q, predicate);
        q = this.applyQueryHandler(q, handler);
        q = this.applySorting(bindings,q, sort);

        var expression = new KcExpression<Map<String, Object>>((Class) Map.class, bindings);

        return q.select(expression).fetchOne();
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
    public <P> P selectOne(Predicate predicate, KcExpression<P> kcExpression) {
        return this.selectOne(predicate, kcExpression, null);
    }

    @Override
    public <P> P selectOne(Predicate predicate, KcExpression<P> kcExpression, KcQueryHandler queryHandler) {
        var q = this.createQuery();

        q = this.applyPredicate(q, predicate);
        q = this.applyQueryHandler(q, queryHandler);

        return q.select(kcExpression).fetchOne();
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
        q = this.applySorting(null, q, sort);

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
        query = this.applyPagination(null, query, pageable);

        return new PageImpl<>(query.select(path).fetch(), pageable, totalSize);
    }

    @Override
    public <P> List<P> selectList(Predicate predicate, KcExpression<P> expression) {
        return this.selectList(predicate, expression, null, null);
    }

    @Override
    public <P> List<P> selectList(Predicate predicate, KcExpression<P> kcExpression, KcQueryHandler queryHandler) {
        return this.selectList(predicate, kcExpression, queryHandler, null);
    }

    @Override
    public <P> List<P> selectList(Predicate predicate, KcExpression<P> kcExpression, KcQueryHandler queryHandler, Sort sort) {
        var q = this.createQuery();

        q = this.applyPredicate(q, predicate);
        q = this.applyQueryHandler(q, queryHandler);
        q = this.applySorting(kcExpression.getBindings(), q, sort);

        return q.select(kcExpression).fetch();
    }

    @Override
    public <P> Page<P> selectPage(Predicate predicate, KcExpression<P> kcExpression, Pageable pageable) {
        return this.selectPage(predicate, kcExpression, pageable, null);
    }

    @Override
    public <P> Page<P> selectPage(Predicate predicate, KcExpression<P> kcExpression, Pageable pageable, KcQueryHandler kcQueryHandler) {

        Assert.notNull(pageable, "pageable must not be null!");

        var query = this.createQuery();

        query = this.applyPredicate(query, predicate);
        query = this.applyQueryHandler(query, kcQueryHandler);

        var totalSize = query.fetch().size();

        query = this.applyPagination(kcExpression.getBindings(), query, pageable);

        return new PageImpl<>(query.select(kcExpression).fetch(), pageable, totalSize);
    }

    @Override
    public <P> List<P> selectList(Predicate predicate, Class<P> classType, Map<String, Expression<?>> bindings) {
        Assert.notNull(classType, "classType must not be null!");
        Assert.notEmpty(bindings, "bindings must not be empty!");

        var expression = new KcExpression<>(classType, bindings);

        return this.selectList(predicate, expression);
    }

    @Override
    public <P> List<P> selectList(Predicate predicate, Class<P> classType, Map<String, Expression<?>> bindings, KcQueryHandler queryHandler) {
        Assert.notNull(classType, "classType must not be null!");
        Assert.notEmpty(bindings, "bindings must not be empty!");

        var expression = new KcExpression<>(classType, bindings);

        return this.selectList(predicate, expression, queryHandler);
    }

    @Override
    public <P> List<P> selectList(Predicate predicate, Class<P> classType, Map<String, Expression<?>> bindings, KcQueryHandler queryHandler, Sort sort) {
        Assert.notNull(classType, "classType must not be null!");
        Assert.notEmpty(bindings, "bindings must not be empty!");

        var expression = new KcExpression<>(classType, bindings);

        return this.selectList(predicate, expression, queryHandler, sort);
    }

    @Override
    public <P> Page<P> selectPage(Predicate predicate, Class<P> classType, Map<String, Expression<?>> bindings, Pageable pageable) {
        Assert.notNull(classType, "classType must not be null!");
        Assert.notEmpty(bindings, "bindings must not be empty!");

        var expression = new KcExpression<>(classType, bindings);

        return this.selectPage(predicate, expression, pageable);
    }

    @Override
    public <P> Page<P> selectPage(Predicate predicate, Class<P> classType, Map<String, Expression<?>> bindings, Pageable pageable, KcQueryHandler kcQueryHandler) {
        Assert.notNull(classType, "classType must not be null!");
        Assert.notEmpty(bindings, "bindings must not be empty!");

        var expression = new KcExpression<>(classType, bindings);

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

    private OrderSpecifier.NullHandling castToQueryDslNullHandling(Sort.NullHandling nullHandling) {
        return switch (nullHandling) {
            case NULLS_FIRST -> OrderSpecifier.NullHandling.NullsFirst;
            case NULLS_LAST -> OrderSpecifier.NullHandling.NullsLast;
            default -> OrderSpecifier.NullHandling.Default;
        };
    }

    private boolean isAlias(Expression<?> expression) {
        return expression instanceof Operation && ((Operation<?>) expression).getOperator() == Ops.ALIAS;
    }

    private String getAlias(Expression<?> expression) {
        if (!this.isAlias(expression)) {
            throw new RuntimeException("expression is not alias");
        }
        var oe = (Operation<?>) expression;
        return oe.getArg(1).toString();
    }

    private JPAQuery<?> applySorting(Map<String, Expression<?>> bindings, JPAQuery<?> query, Sort sort) {
        if (sort != null) {
            if (sort instanceof QSort qSort) {
                var orderSpecifiers = qSort.getOrderSpecifiers();
                query = query.orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]));
            } else {
                for (var order : sort) {
                    OrderSpecifier.NullHandling nullHandling = this.castToQueryDslNullHandling(order.getNullHandling());
                    OrderSpecifier<?> os;
                    if (bindings != null && bindings.containsKey(order.getProperty())) {
                        var expression = bindings.get(order.getProperty());

                        // 별칭
                        if (this.isAlias(expression)) {
                            os = new OrderSpecifier<>(order.isAscending() ? Order.ASC : Order.DESC, Expressions.stringPath(this.getAlias(expression)), nullHandling);
                        }
                        // 아닌경우 표현식
                        else {
                            os = new OrderSpecifier(order.isAscending() ? Order.ASC : Order.DESC, expression, nullHandling);
                        }

                    } else {
                        os = new OrderSpecifier<>(order.isAscending() ? Order.ASC : Order.DESC, Expressions.stringPath(order.getProperty()), nullHandling);
                    }

                    query = query.orderBy(os);
                }
            }
        }

        return query;
    }

    private JPAQuery<?> applyPagination(Map<String, Expression<?>> bindings, JPAQuery<?> query, Pageable pageable) {
        if (pageable != null) {
            query = query.offset(pageable.getOffset()).limit(pageable.getPageSize());

            return this.applySorting(bindings, query, pageable.getSort());
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

    // delete clause
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public JPADeleteClause createDeleteClause() {
        return new JPADeleteClause(entityManager, path);
    }

    @Override
    public long deleteOne(Predicate predicate) {
        Assert.notNull(predicate, "predicate must not be null");

        var result = this.createDeleteClause()
                .where(predicate)
                .execute();

        Assert.isTrue(result == 1, "update result is not 1");

        return result;
    }
}
