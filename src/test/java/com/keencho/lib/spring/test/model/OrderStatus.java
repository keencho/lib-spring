package com.keencho.lib.spring.test.model;

public enum OrderStatus {
    RECEIVED("접수"),
    PACKAGING("포장"),
    SHIPPING("배송중"),
    COMPLETED("완료"),
    FAILED("실패");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }
}
