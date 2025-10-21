package com.deliverysystem.orders.client.representation;

import java.math.BigDecimal;
import java.util.UUID;

public record MenuRepresentationDTO(
        UUID id,
        String description,
        BigDecimal price,
        String menuType) {
}
