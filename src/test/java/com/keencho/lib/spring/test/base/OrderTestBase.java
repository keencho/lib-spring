package com.keencho.lib.spring.test.base;

import com.keencho.lib.spring.jpa.querydsl.KcQBean;
import com.keencho.lib.spring.test.model.*;
import com.keencho.lib.spring.test.utils.DataGenerator;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import org.junit.jupiter.api.BeforeAll;

import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

public class OrderTestBase extends JPATestBase {

    private static List<OrderStatus> orderStatusList = Arrays.asList(OrderStatus.values());
    private static Random rand = new Random();

    protected static int targetRowNum = 500;
    protected static Map<Class<? extends Order>, Map.Entry<EntityPathBase<? extends Order>, PathBuilder<? extends Order>>> entityPathMap = new HashMap<>();

    private static <T extends Order> T generateOrder(int year, int month, Class<T> targetClass) {
        T order;
        try {
            order = targetClass.getDeclaredConstructor().newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("class initialization failed ");
        }

        order.setStatus(orderStatusList.get(rand.nextInt(orderStatusList.size())));

        order.setFromAddress(DataGenerator.address());
        order.setFromName(DataGenerator.name());
        order.setFromPhoneNumber(DataGenerator.phone());

        order.setToAddress(DataGenerator.address());
        order.setToName(DataGenerator.name());
        order.setToPhoneNumber(DataGenerator.phone());

        order.setItemName(DataGenerator.itemName());
        order.setItemPrice(Integer.parseInt(rand.nextInt(10, 100) + "0000"));
        order.setCreatedDateTime(LocalDateTime.of(year, month, rand.nextInt(1,31), rand.nextInt(24), rand.nextInt(60), rand.nextInt(60)));

        return order;
    }

    @BeforeAll
    public static void beforeAll() {
        IntStream.range(0, targetRowNum).forEach(idx -> entityManager.persist(generateOrder(2022, 6, Order_2206.class)));
        IntStream.range(0, targetRowNum).forEach(idx -> entityManager.persist(generateOrder(2022, 9, Order_2209.class)));
        IntStream.range(0, targetRowNum).forEach(idx -> entityManager.persist(generateOrder(2023, 1, Order_2301.class)));

        entityPathMap.put(Order_2206.class, new AbstractMap.SimpleEntry<>(new EntityPathBase<>(Order_2206.class, "entity"), new PathBuilder<>(Order_2206.class, "entity")));
        entityPathMap.put(Order_2209.class, new AbstractMap.SimpleEntry<>(new EntityPathBase<>(Order_2209.class, "entity"), new PathBuilder<>(Order_2209.class, "entity")));
        entityPathMap.put(Order_2301.class, new AbstractMap.SimpleEntry<>(new EntityPathBase<>(Order_2301.class, "entity"), new PathBuilder<>(Order_2301.class, "entity")));

        entityManager.getTransaction().commit();
    }

    public static <E extends Order, P> KcQBean<P> buildKcQBean(Class<E> entityClass, Class<P> projectionClass, String... excludeFields) {
        var bindings = new HashMap<String, Expression<?>>();

        var entry = entityPathMap.get(Order_2206.class);
        var path = entry.getValue();

        for (var projectionField : projectionClass.getDeclaredFields()) {
            if (Arrays.stream(excludeFields).anyMatch(name -> name.equals(projectionField.getName()))) {
                continue;
            }
            for (var entityField : entityClass.getSuperclass().getDeclaredFields()) {
                var modifiers = projectionField.getModifiers();

                if (!Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers)) {
                    if (projectionField.getName().equals(entityField.getName())) {
                        bindings.put(projectionField.getName(), path.get(entityField.getName(), entityField.getType()));
                        break;
                    }
                }
            }
        }

        return new KcQBean<>(projectionClass, bindings);
    }

}
