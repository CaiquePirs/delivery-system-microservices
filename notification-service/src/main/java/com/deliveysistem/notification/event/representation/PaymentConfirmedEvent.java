package com.deliveysistem.notification.event.representation;

import java.math.BigDecimal;

public record PaymentConfirmedEvent(
        String id,
        String orderId,
        BigDecimal amount,
        String status) {
}
