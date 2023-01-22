package base;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQueryFactory;
import com.keencho.lib.spring.jpa.querydsl.KcQuerydslAnnotationProcessor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;

public class JPATestBase {

    protected static EntityManagerFactory entityManagerFactory;
    protected static EntityManager entityManager;
    protected static CriteriaBuilderFactory criteriaBuilderFactory;

    protected BlazeJPAQueryFactory blazeJPAQueryFactory() {
        return new BlazeJPAQueryFactory(entityManager, criteriaBuilderFactory);
    }

    static void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("pu");
        entityManager = entityManagerFactory.createEntityManager();

        var config = Criteria.getDefault();
        criteriaBuilderFactory = config.createCriteriaBuilderFactory(entityManagerFactory);

        entityManager.getTransaction().begin();

        var a = new KcQuerydslAnnotationProcessor();
    }

    @AfterAll
    static void afterAll() {
        entityManager.close();
        entityManagerFactory.close();
    }

}
