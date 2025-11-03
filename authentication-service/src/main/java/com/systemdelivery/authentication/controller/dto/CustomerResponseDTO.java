package com.systemdelivery.authentication.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CustomerResponseDTO(
        UUID id,
        String name,
        String email) {
}
