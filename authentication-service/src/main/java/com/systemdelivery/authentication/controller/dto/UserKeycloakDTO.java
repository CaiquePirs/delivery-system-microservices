package com.systemdelivery.authentication.controller.dto;

import com.systemdelivery.authentication.model.UserRoleType;
import lombok.Builder;

@Builder
public record UserKeycloakDTO(
        String firstName,
        String lastName,
        String email,
        String password,
        UserRoleType role) {
}
