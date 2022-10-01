package com.keencho.lib.spring.jpa.querydsl.repository;

import com.keencho.lib.spring.jpa.querydsl.KcQBean;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.util.List;

public class KcSearchQueryImpl<T> implements KcSearchQuery<T> {
    private final JPAQueryFactory queryFactory;
    private final EntityPath<T> path;
    private final PathBuilder<?> pathBuilder;

    public KcSearchQueryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
        this.path = SimpleEntityPathResolver.INSTANCE.createPath(entityInformation.getJavaType());
        this.pathBuilder = new PathBuilder<T>(path.getType(), path.getMetadata());
    }

    @Override
    public List<T> findList(Predicate predicate, Sort sort) {
        var q = this.createQuery();

        q = this.applyPredicate(q, predicate);
        q = this.applySorting(q, sort);

        return q.select(path).fetch();
    }

    @Override
    public <P> List<P> selectList(Predicate predicate, FactoryExpressionBase<P> factoryExpressionBase, Sort sort) {
        var q = this.createQuery();

         q = this.applyPredicate(q, predicate);
         q = this.applySorting(q, sort);

        if (factoryExpressionBase instanceof KcQBean<P> kc) {
            Assert.isTrue(!kc.isBuild, "GFQBean instance must not be build. build should execute at runtime.");
            factoryExpressionBase = kc.build();
        }

        return q.select(factoryExpressionBase).fetch();
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
}
