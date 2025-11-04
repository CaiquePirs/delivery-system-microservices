package com.systemdelivery.authentication.controller.dto;

import com.systemdelivery.authentication.model.UserRoleType;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserKeycloakDTO(
        String firstName,
        String lastName,
        String email,
        String password,
        UUID userCreatedId,
        UserRoleType role) {
}
