package com.keencho.lib.spring.jpa.querydsl.repository;

import com.keencho.lib.spring.jpa.querydsl.KcQBean;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.FactoryExpressionBase;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.querydsl.QSort;
import org.springframework.data.querydsl.SimpleEntityPathResolver;

import javax.persistence.EntityManager;
import java.util.List;

public class KcSearchQueryImpl<T> implements KcSearchQuery<T> {
    private final JPAQueryFactory queryFactory;
    private final EntityPath<T> path;

    public KcSearchQueryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
        this.path = SimpleEntityPathResolver.INSTANCE.createPath(entityInformation.getJavaType());
    }

    @Override
    public List<T> findList(Predicate predicate) {
        return this.findList(predicate, null);
    }

    @Override
    public List<T> findList(Predicate predicate, QSort sort) {
        return queryFactory
                .selectFrom(path)
                .where(predicate)
//                .orderBy(sort)
                .fetch();
    }

    @Override
    public <P> List<P> selectList(Predicate predicate, FactoryExpressionBase<P> fb) {

        if (fb instanceof KcQBean<P> kc) {
            fb = kc.build();
        }

        return queryFactory
                .select(fb)
                .from(path)
                .where(predicate)
                .fetch();
    }
}
