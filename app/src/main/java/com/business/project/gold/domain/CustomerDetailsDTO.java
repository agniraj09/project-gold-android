package com.business.project.gold.domain;

public class CustomerDetailsDTO {

    private String customerId;
    private String customerName;
    private String mobileNumber;
    private String email;

    public String getCustomerId() {
        return customerId;
    }

    public CustomerDetailsDTO setCustomerId(String customerId) {
        this.customerId = customerId;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }

    public CustomerDetailsDTO setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public CustomerDetailsDTO setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public CustomerDetailsDTO setEmail(String email) {
        this.email = email;
        return this;
    }
}
