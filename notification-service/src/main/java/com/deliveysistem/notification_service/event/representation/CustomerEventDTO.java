package com.deliveysistem.notification_service.event.representation;

import java.util.UUID;

public record CustomerEventDTO(
        UUID id,
        String name,
        String email,
        String phone,
        AddressEventDTO deliveryAddress) {
}
