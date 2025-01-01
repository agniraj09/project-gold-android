package com.business.project.gold.domain;

import java.io.Serializable;

public record CouponCodeDetailsDto(String code, String expiryDate,
                                   String discountPercentage) implements Serializable {
}
