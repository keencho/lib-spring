package com.keencho.lib.spring.test.unit.jpa;

import com.keencho.lib.spring.test.annotation.SelectExcludeField;
import com.keencho.lib.spring.test.base.OrderTestBase;
import com.keencho.lib.spring.test.dto.OrderDTO;
import com.keencho.lib.spring.test.model.OrderStatus;
import com.keencho.lib.spring.test.model.Order_2206;
import com.keencho.lib.spring.test.repository.Order_2206Repository;
import com.keencho.lib.spring.test.utils.SpringUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.types.Expression;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.querydsl.QSort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

import static com.keencho.lib.spring.test.utils.KcAssertions.*;

public class KcCustomSpringDataJPARepositoryTest extends OrderTestBase {

    Order_2206Repository order_2206Repository = jpaRepositoryFactory().getRepository(Order_2206Repository.class);

    @Test
    @DisplayName("데이터 정합성 검증 (Hibernate, Spring Data JPA, Custom QueryDSL Method)")
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

        assertMultipleEquals(order1.size(), order2.size(), order3.size());
    }

    @Test
    @DisplayName("KcSearchQuery - findOne")
    void KcSearchQuery_findOne() {

        var thrown = assertThrows(NonUniqueResultException.class, () -> {
            var path = getPathBuilder(Order_2206.class);

            order_2206Repository.findOne(path.getString("toName").contains("김"));
        });

        assertTrue(thrown.getMessage().contains("query did not return a unique result"));
    }

    @Test
    @DisplayName("KcSearchQuery - findList")
    void KcSearchQuery_findList() {

        var targetStatus = OrderStatus.COMPLETED;
        var targetPrice = 500000;

        //////////////////// findAll() -> filter, sort data
        var order1 = order_2206Repository.findAll()
                .stream()
                .filter(order -> order.getStatus() == targetStatus)
                .filter(order -> order.getItemPrice() < targetPrice)
                .sorted(Comparator.comparing(Order_2206::getToName).reversed())
                .toList();

        //////////////////// Custom QueryDSL Method
        var bb = new BooleanBuilder();
        var path = getPathBuilder(Order_2206.class);

        bb.and(path.get("status", OrderStatus.class).eq(targetStatus));
        bb.and(path.getNumber("itemPrice", Integer.class).lt(targetPrice));

        var order2 = order_2206Repository.findList(bb, null, new QSort(path.getString("toName").desc()));

        assertEquals(
                order1.stream().map(Order_2206::getToName).collect(Collectors.joining(", ")),
                order2.stream().map(Order_2206::getToName).collect(Collectors.joining(", "))
        );
    }

    @Test
    @DisplayName("KcSearchQuery - findPage")
    void KcSearchQuery_findPage() {

        var targetStatus = OrderStatus.COMPLETED;

        //////////////////// findAll() -> filter, sort data
        var targetList = order_2206Repository.findAll()
                .stream()
                .filter(order -> order.getStatus() == targetStatus)
                .sorted(Comparator.comparing(Order_2206::getToName).reversed())
                .toList();

        //////////////////// Custom QueryDSL Method
        var bb = new BooleanBuilder();
        var path = getPathBuilder(Order_2206.class);

        bb.and(path.get("status", OrderStatus.class).eq(OrderStatus.COMPLETED));

        var sort = new QSort(path.getString("toName").desc());

        var pageList = new ArrayList<Order_2206>();

        var pageSize = 10;
        var offset = 0;
        var resultSize = 0;

        // 전체 갯수에 도달할때까지 offset += 10 하면서 반복
        while(true) {
            var page = order_2206Repository.findPage(bb, SpringUtils.buildPageable(pageSize, offset, sort));

            resultSize += page.getNumberOfElements();
            pageList.addAll(page.getContent());

            if (pageList.size() >= page.getTotalElements()) break;

            offset += pageSize;
        }

        assertEquals(targetList.size(), resultSize);
        assertEquals(
                targetList.stream().map(Order_2206::getToName).collect(Collectors.joining(", ")),
                pageList.stream().map(Order_2206::getToName).collect(Collectors.joining(", "))
        );
    }

    @Test
    @DisplayName("KcSearchQuery - selectOne")
    void KcSearchQuery_selectOne() {
        var thrown = assertThrows(NonUniqueResultException.class, () -> {
            var path = getPathBuilder(Order_2206.class);

            var bean = buildKcQBean(Order_2206.class, OrderDTO.class);

            order_2206Repository.selectOne(path.getString("toName").contains("김"), bean);
        });

        assertTrue(thrown.getMessage().contains("query did not return a unique result"));
    }

    @Test
    @DisplayName("KcSearchQuery - selectList")
    void KcSearchQuery_selectList() {

        // 1. 조회 조건 정의
        var bb = new BooleanBuilder();
        var path = getPathBuilder(Order_2206.class);

        bb.and(path.get("status", OrderStatus.class).eq(OrderStatus.COMPLETED));
        bb.and(path.getNumber("itemPrice", Integer.class).lt(500000));

        // 2. KcQBean 조회 대상 정의
        var projection = buildKcQBean(Order_2206.class, OrderDTO.class);

        // 3. Expression Map 조회 대상 정의
        var map = new HashMap<String, Expression<?>>();
        for (var field : OrderDTO.class.getDeclaredFields()) {
            if (!field.isAnnotationPresent(SelectExcludeField.class)) {
                map.put(field.getName(), path.get(field.getName(), field.getType()));
            }
        }

        // 4. 조회 후 검증
        validateOrderDTO(order_2206Repository.selectList(bb, projection));
        validateOrderDTO(order_2206Repository.selectList(bb, OrderDTO.class, map));
    }

    @Test
    @DisplayName("KcSearchQuery - selectPage")
    void KcSearchQuery_selectPage() {
        var targetStatus = OrderStatus.COMPLETED;

        //////////////////// selectAll -> filter, sort data -> model mapper mapping
        var targetList = order_2206Repository.findAll()
                .stream()
                .filter(order -> order.getStatus() == targetStatus)
                .sorted(Comparator.comparing(Order_2206::getToName).reversed())
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .toList();

        //////////////////// Custom QueryDSL Method
        var bb = new BooleanBuilder();
        var path = getPathBuilder(Order_2206.class);

        bb.and(path.get("status", OrderStatus.class).eq(OrderStatus.COMPLETED));

        var sort = new QSort(path.getString("toName").desc());

        var kcQBeanSelectList = new ArrayList<OrderDTO>();
        var expressionMapSelectList = new ArrayList<OrderDTO>();

        var pageSize = 10;
        var offset = 0;

        var projection = buildKcQBean(Order_2206.class, OrderDTO.class);

        var map = new HashMap<String, Expression<?>>();
        for (var field : OrderDTO.class.getDeclaredFields()) {
            if (!field.isAnnotationPresent(SelectExcludeField.class)) {
                map.put(field.getName(), path.get(field.getName(), field.getType()));
            }
        }

        while(true) {
            var page1 = order_2206Repository.selectPage(bb, projection, SpringUtils.buildPageable(pageSize, offset, sort));
            var page2 = order_2206Repository.selectPage(bb, OrderDTO.class, map, SpringUtils.buildPageable(pageSize, offset, sort));

            // 1차로 단순 list size 만 검증
            assertEquals(page1.getNumberOfElements(), page2.getNumberOfElements());

            kcQBeanSelectList.addAll(page1.getContent());
            expressionMapSelectList.addAll(page2.getContent());

            if (kcQBeanSelectList.size() >= page1.getTotalElements()) break;

            offset += pageSize;
        }

        validateOrderDTO(kcQBeanSelectList);
        validateOrderDTO(expressionMapSelectList);

        var joinDelimiter = ", ";

        assertMultipleEquals(targetList.size(), kcQBeanSelectList.size(), expressionMapSelectList.size());
        assertMultipleEquals(
                targetList.stream().map(OrderDTO::getToName).collect(Collectors.joining(joinDelimiter)),
                expressionMapSelectList.stream().map(OrderDTO::getToName).collect(Collectors.joining(joinDelimiter)),
                kcQBeanSelectList.stream().map(OrderDTO::getToName).collect(Collectors.joining(joinDelimiter))
        );
    }
}
