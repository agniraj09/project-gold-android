package com.business.project.gold.domain;

import java.time.LocalDate;

public class OrderIDAndDate {

    private Long orderId;
    private String functionDate;

    public Long getOrderId() {
        return orderId;
    }

    public OrderIDAndDate setOrderId(Long orderId) {
        this.orderId = orderId;
        return this;
    }

    public String getFunctionDate() {
        return functionDate;
    }

    public OrderIDAndDate setFunctionDate(String functionDate) {
        this.functionDate = functionDate;
        return this;
    }
}
