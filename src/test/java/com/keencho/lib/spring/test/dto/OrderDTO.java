package com.keencho.lib.spring.test.dto;

import com.keencho.lib.spring.jpa.querydsl.annotation.KcQueryProjection;
import com.keencho.lib.spring.test.annotation.SelectExcludeField;
import com.keencho.lib.spring.test.model.OrderStatus;

import java.time.LocalDateTime;

@KcQueryProjection
public class OrderDTO {
    @SelectExcludeField
    String id;
    @SelectExcludeField
    OrderStatus status;
    String fromAddress;
    String fromName;
    String fromPhoneNumber;

    String toAddress;
    String toName;
    String toPhoneNumber;

    @SelectExcludeField
    String itemName;
    @SelectExcludeField
    int itemPrice;

    LocalDateTime createdDateTime;

    public OrderDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getFromPhoneNumber() {
        return fromPhoneNumber;
    }

    public void setFromPhoneNumber(String fromPhoneNumber) {
        this.fromPhoneNumber = fromPhoneNumber;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getToPhoneNumber() {
        return toPhoneNumber;
    }

    public void setToPhoneNumber(String toPhoneNumber) {
        this.toPhoneNumber = toPhoneNumber;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }
}
