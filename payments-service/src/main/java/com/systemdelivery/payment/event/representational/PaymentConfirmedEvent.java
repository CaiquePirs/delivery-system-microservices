package com.systemdelivery.payment.event.representational;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record PaymentConfirmedEvent(
        String id,
        String orderId,
        BigDecimal amount,
        String status) {
}
