package com.keencho.lib.spring.test.base;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class JPATestBase {

    protected static EntityManagerFactory entityManagerFactory;
    protected static EntityManager entityManager;
    protected static CriteriaBuilderFactory criteriaBuilderFactory;

    // blaze-persistence factory
    protected BlazeJPAQueryFactory blazeJPAQueryFactory() {
        return new BlazeJPAQueryFactory(entityManager, criteriaBuilderFactory);
    }

    // query-dsl factory
    protected JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @BeforeAll
    public static void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("pu");
        entityManager = entityManagerFactory.createEntityManager();

        var config = Criteria.getDefault();
        criteriaBuilderFactory = config.createCriteriaBuilderFactory(entityManagerFactory);

        entityManager.getTransaction().begin();
    }

    @AfterAll
    public static void afterAll() {
        entityManager.close();
        entityManagerFactory.close();
    }

}
