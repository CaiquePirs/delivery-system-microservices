package com.systemdelivery.payment.event.representational;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public class PaymentConfirmedEvent {
    private String id;
    private UUID orderId;
    private BigDecimal amount;
    private String status;
}
