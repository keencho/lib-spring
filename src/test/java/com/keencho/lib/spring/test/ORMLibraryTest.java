package com.keencho.lib.spring.test;

import com.keencho.lib.spring.test.base.OrderTestBase;
import com.keencho.lib.spring.test.model.Order_2206;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ORMLibraryTest extends OrderTestBase {

    @Test
    @DisplayName("기초 쿼리 테스트 (Hibernate, QueryDSL)")
    public void test() {

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

        var entry = entityPathMap.get(Order_2206.class);
        var entity = entry.getKey();
        var path = entry.getValue();

        var order2 = jpaQueryFactory()
                .selectFrom(entity)
                .where(path.getString(property).like(value))
                .fetch();

        assertEquals(order1.size(), order2.size());
    }
}
