package com.business.project.gold.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class NewOrderRequest {

    private String functionDate;

    private String type;

    private BigDecimal advanceAmount;

    private BigDecimal totalAmount;

    private BigDecimal damageRepairCost;

    private BigDecimal deliveryCharges;

    private Long referrer;

    private Long manager;

    private BigDecimal cancellationCharge;

    private CustomerDetailsDTO customerDetails;

    public CustomerDetailsDTO getCustomerDetails() {
        return customerDetails;
    }

    public NewOrderRequest setCustomerDetails(CustomerDetailsDTO customerDetails) {
        this.customerDetails = customerDetails;
        return this;
    }

    public String getFunctionDate() {
        return functionDate;
    }

    public NewOrderRequest setFunctionDate(String functionDate) {
        this.functionDate = functionDate;
        return this;
    }

    public String getType() {
        return type;
    }

    public NewOrderRequest setType(String type) {
        this.type = type;
        return this;
    }

    public BigDecimal getAdvanceAmount() {
        return advanceAmount;
    }

    public NewOrderRequest setAdvanceAmount(BigDecimal advanceAmount) {
        this.advanceAmount = advanceAmount;
        return this;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public NewOrderRequest setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    public BigDecimal getDamageRepairCost() {
        return damageRepairCost;
    }

    public NewOrderRequest setDamageRepairCost(BigDecimal damageRepairCost) {
        this.damageRepairCost = damageRepairCost;
        return this;
    }

    public BigDecimal getDeliveryCharges() {
        return deliveryCharges;
    }

    public NewOrderRequest setDeliveryCharges(BigDecimal deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
        return this;
    }

    public Long getReferrer() {
        return referrer;
    }

    public NewOrderRequest setReferrer(Long referrer) {
        this.referrer = referrer;
        return this;
    }

    public Long getManager() {
        return manager;
    }

    public NewOrderRequest setManager(Long manager) {
        this.manager = manager;
        return this;
    }

    public BigDecimal getCancellationCharge() {
        return cancellationCharge;
    }

    public NewOrderRequest setCancellationCharge(BigDecimal cancellationCharge) {
        this.cancellationCharge = cancellationCharge;
        return this;
    }
}