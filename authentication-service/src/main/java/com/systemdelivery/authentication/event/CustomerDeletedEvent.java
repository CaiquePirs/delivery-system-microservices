package com.systemdelivery.authentication.event;

import java.util.UUID;

public record CustomerDeletedEvent(
        UUID customerId,
        String email,
        String status) {
}
