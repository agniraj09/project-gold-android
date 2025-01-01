package com.business.project.gold.domain;

import java.math.BigDecimal;

public record InvestorRevenueDetails(
        Long id, String firstName, String lastName, String fullName, Long totalOrders,
        BigDecimal totalIncome) {
}
