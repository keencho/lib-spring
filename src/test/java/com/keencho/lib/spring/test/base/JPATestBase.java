package com.keencho.lib.spring.test.base;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQueryFactory;
import com.keencho.lib.spring.jpa.querydsl.repository.KcDefaultJPAQuery;
import com.keencho.lib.spring.jpa.querydsl.repository.KcSearchQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFragment;

public class JPATestBase {

    protected static EntityManagerFactory entityManagerFactory;
    protected static EntityManager entityManager;
    protected static CriteriaBuilderFactory criteriaBuilderFactory;
    protected static ModelMapper modelMapper = new ModelMapper();

    // blaze-persistence factory
    protected BlazeJPAQueryFactory blazeJPAQueryFactory() {
        return new BlazeJPAQueryFactory(entityManager, criteriaBuilderFactory);
    }

    // query-dsl factory
    protected JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    // spring data jpa repository factory
    protected JpaRepositoryFactory jpaRepositoryFactory() {
        return new JpaRepositoryFactory(entityManager) {
            @Override
            protected RepositoryComposition.RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {
                var fragments = super.getRepositoryFragments(metadata);

                if (KcSearchQuery.class.isAssignableFrom(metadata.getRepositoryInterface())) {
                    var impl = super.instantiateClass(
                            KcDefaultJPAQuery.class,
                            metadata.getDomainType(), entityManager
                    );

                    fragments = fragments.append(RepositoryFragment.implemented(impl));
                }

                return fragments;
            }
        };
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
