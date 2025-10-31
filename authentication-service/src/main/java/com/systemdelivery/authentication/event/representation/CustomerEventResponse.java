package com.systemdelivery.authentication.event.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CustomerEventResponse(
        UUID id,
        String name,
        String email,
        String phone,
        RegisterEventStatus status
) {
}
