import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQueryFactory;
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

    static void setupDB() {
        entityManagerFactory = Persistence.createEntityManagerFactory("pu");
        entityManager = entityManagerFactory.createEntityManager();

        var config = Criteria.getDefault();
        criteriaBuilderFactory = config.createCriteriaBuilderFactory(entityManagerFactory);

        entityManager.getTransaction().begin();
    }

    @AfterAll
    static void afterAll() {
        entityManager.close();
        entityManagerFactory.close();
    }

}
