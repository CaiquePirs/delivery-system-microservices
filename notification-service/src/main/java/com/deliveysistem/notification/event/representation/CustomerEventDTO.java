package com.deliveysistem.notification.event.representation;

import java.util.UUID;

public record CustomerEventDTO(
        UUID id,
        String name,
        String email,
        String phone,
        AddressEvent deliveryAddress) {
}
