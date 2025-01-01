package com.business.project.gold.domain;

import java.io.Serializable;

/**
 * DTO for {@link UserDetails}
 */
public record UserDetailsWithNameDTO(
        Long id,
        String firstName,
        String lastName,

        String fullName,
        String role)
        implements Serializable {
}
