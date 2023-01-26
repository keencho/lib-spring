package com.keencho.lib.spring.test;

import com.keencho.lib.spring.test.base.OrderTestBase;
import com.keencho.lib.spring.test.model.Order_2206;
import com.keencho.lib.spring.test.repository.Order_2206Repository;
import com.querydsl.core.BooleanBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KcCustomSpringDataJPARepositoryTest extends OrderTestBase {

    Order_2206Repository order_2206Repository = jpaRepositoryFactory().getRepository(Order_2206Repository.class);

    @Test
    @DisplayName("기초 쿼리 테스트 (Hibernate, Spring Data JPA, Custom QueryDSL Method)")
    void defaultQueryTest() {

        //////////////////// Hibernate
        var property = "toName";
        var value = "김%";

        var query = """
                SELECT o
                FROM Order_2206 o
                WHERE o.toName like :toName
                """;

        var order1 = entityManager
                .createQuery(query)
                .setParameter(property, value)
                .getResultList();

        //////////////////// Spring Data JPA
        var order2 = order_2206Repository.findByToNameLike(value);

        //////////////////// Custom QueryDSL Method
        var path = getPathBuilder(Order_2206.class);

        var bb = new BooleanBuilder();

        bb.and(path.getString(property).like(value));

        var order3 = order_2206Repository.findList(bb);

        assertEquals(order1.size(), order2.size(), order3.size());
    }


}
