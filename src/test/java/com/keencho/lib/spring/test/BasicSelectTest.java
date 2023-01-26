package com.keencho.lib.spring.test;

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
    // 이 테스트코드 안에서 KcQ 클래스를 생성하여 테스트하기는 힘드므로 (순환참조) 명시적으로 바인딩하여 KcQBean이 Projection 으로써 잘 동작하는지만 테스트한다.
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
        // id, itemName, itemPrice 필드는 조회조건에서 제외
        var bean = buildKcQBean(Order_2206.class, OrderDTO.class, "id", "itemName", "itemPrice");

        var order2 = jpaQueryFactory()
                .select(bean)
                .from(entity)
                .where(path.getString(property).like(value))
                .fetch();

        assertEquals(order1.size(), order2.size());
    }
}
