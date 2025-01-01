package com.business.project.gold.domain;

public class SpinnerItem {
    private Long key;
    private String value;

    public Long getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public SpinnerItem setKey(Long key) {
        this.key = key;
        return this;
    }

    public SpinnerItem setValue(String value) {
        this.value = value;
        return this;
    }
}
