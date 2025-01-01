package com.business.project.gold.domain;

import java.math.BigDecimal;
import java.util.List;

public record CumulativeRevenueDetails(
        Long totalOrders,
        Long openOrders,
        Long settledOrders,
        Long cancelledOrders,
        BigDecimal totalIncome,
        Double averageParticipantShare,
        Double averageManagerShare,
        Double averageReferrerShare,
        List<InvestorRevenueDetails> investorRevenueDetails) {
}

