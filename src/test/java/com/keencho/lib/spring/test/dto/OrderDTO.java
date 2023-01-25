package com.keencho.lib.spring.test.dto;

import com.keencho.lib.spring.jpa.querydsl.annotation.KcQueryProjection;
import com.keencho.lib.spring.test.model.OrderStatus;

import java.time.LocalDateTime;

@KcQueryProjection
public class OrderDTO {
    String id;
    OrderStatus orderStatus;
    String fromAddress;
    String fromName;
    String fromPhoneNumber;

    String toAddress;
    String toName;
    String toPhoneNumber;

    String itemName;
    int itemPrice;

    LocalDateTime createdDateTime;

    public OrderDTO() {
    }
}
