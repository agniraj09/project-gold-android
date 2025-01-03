package com.business.project.gold.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public record OrderDetailsWithUserDetailsDTO(
        Long id,
        String functionDate,
        BigDecimal damageRepairCost,
        BigDecimal deliveryCharges,
        BigDecimal cancellationCharges,
        String orderDate,
        String type,
        String status,
        UserDetailsWithNameDTO referrer,
        UserDetailsWithNameDTO manager,
        BigDecimal advanceAmount,
        BigDecimal totalAmount,
        BigDecimal netIncome,
        BigDecimal participantsShare,
        BigDecimal managerShare,
        BigDecimal referrerShare,

        CustomerDetailsDTO customer,

        List<ArtifactGroup> artifactGroups)
        implements Serializable {
}