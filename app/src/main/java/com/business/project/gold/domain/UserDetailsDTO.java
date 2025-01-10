package com.business.project.gold.domain;

import java.io.Serializable;
import java.time.LocalDate;

public record UserDetailsDTO(
        Long id,
        String username,
        String firstName,
        String lastName,
        String fullname,
        String gender,
        String role,
        LocalDate onboardedDate,
        String status)
        implements Serializable {
}
