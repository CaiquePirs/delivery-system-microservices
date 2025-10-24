package com.deliverysystem.delivery.event.representation;

import java.math.BigDecimal;

public record PaymentApprovedEvent(
        String id,
        String orderId,
        BigDecimal amount,
        String status) {
}
