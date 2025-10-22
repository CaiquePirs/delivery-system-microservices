package com.deliveysistem.notification.event.representation;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemsOrderEventDTO(
        String id,
        Integer quantity,
        BigDecimal total,
        UUID menuId) {
}
