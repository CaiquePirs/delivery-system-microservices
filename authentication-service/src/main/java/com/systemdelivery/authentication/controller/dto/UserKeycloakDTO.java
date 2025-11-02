package com.systemdelivery.authentication.controller.dto;

import com.systemdelivery.authentication.event.representation.enums.UserType;
import lombok.Builder;

@Builder
public record UserKeycloakDTO(
        String firstName,
        String lastName,
        String email,
        String password,
        UserType role) {
}
