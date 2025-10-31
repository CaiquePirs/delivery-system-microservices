package com.customers.event.representation;

import lombok.Builder;
import java.util.UUID;

@Builder
public record CustomerEventResponse(
        UUID id,
        String name,
        String email,
        String phone,
        RegisterEventStatus status) {
}
