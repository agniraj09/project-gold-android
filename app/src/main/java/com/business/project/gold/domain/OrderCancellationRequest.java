package com.business.project.gold.domain;

import java.math.BigDecimal;

public record OrderCancellationRequest(
        Long orderId, BigDecimal cancellationCharge, boolean isAdvanceReturned) {
}