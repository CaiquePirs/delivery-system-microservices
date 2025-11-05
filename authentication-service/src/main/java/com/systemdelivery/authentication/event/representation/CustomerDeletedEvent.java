package com.systemdelivery.authentication.event.representation;

import java.util.UUID;

public record CustomerDeletedEvent(
        UUID customerId,
        String email,
        String status) {
}
