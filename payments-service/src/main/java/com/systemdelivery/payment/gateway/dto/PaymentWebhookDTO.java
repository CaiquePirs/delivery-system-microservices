package com.systemdelivery.payment.gateway.dto;

import com.systemdelivery.payment.model.enums.PaymentStatus;
import lombok.Builder;

@Builder
public record PaymentWebhookDTO(
        String paymentId,
        PaymentStatus status,
        String notes,
        String paymentKey) {
}
