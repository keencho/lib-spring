import model.*;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import utils.DataGenerator;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class OrderTestBase extends JPATestBase {

    static List<OrderStatus> orderStatusList = Arrays.asList(OrderStatus.values());
    static Random rand = new Random();
    static int rowNum = 500;

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
        if (entityManager == null) {
            setupDB();
        }
        IntStream.range(0, rowNum).forEach(idx -> entityManager.persist(generateOrder(2022, 6, Order_2206.class)));
        IntStream.range(0, rowNum).forEach(idx -> entityManager.persist(generateOrder(2022, 9, Order_2209.class)));
        IntStream.range(0, rowNum).forEach(idx -> entityManager.persist(generateOrder(2023, 1, Order_2301.class)));

        entityManager.getTransaction().commit();
    }

}
