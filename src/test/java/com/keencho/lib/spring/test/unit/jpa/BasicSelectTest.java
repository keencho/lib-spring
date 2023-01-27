package com.keencho.lib.spring.test.unit.jpa;

import com.keencho.lib.spring.test.base.OrderTestBase;
import com.keencho.lib.spring.test.dto.OrderDTO;
import com.keencho.lib.spring.test.model.Order_2206;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicSelectTest extends OrderTestBase {

    @Test
    @DisplayName("기초 쿼리 테스트 (Hibernate, QueryDSL)")
    public void defaultQueryTest() {

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

        var entity = getEntityPath(Order_2206.class);
        var path = getPathBuilder(Order_2206.class);

        var order2 = jpaQueryFactory()
                .selectFrom(entity)
                .where(path.getString(property).like(value))
                .fetch();

        assertEquals(order1.size(), order2.size());
    }

    @Test
    @DisplayName("KcQBean 테스트")
    public void kcQBeanTest() {

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

        var entity = getEntityPath(Order_2206.class);
        var path = getPathBuilder(Order_2206.class);
        var bean = buildKcQBean(Order_2206.class, OrderDTO.class);

        var order2 = jpaQueryFactory()
                .select(bean)
                .from(entity)
                .where(path.getString(property).like(value))
                .fetch();

        assertEquals(order1.size(), order2.size());
        validateOrderDTO(order2);
    }
}
