package com.systemdelivery.payment.event.representational;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.systemdelivery.payment.model.PaymentData;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProcessOrderPaymentEvent(
        String id,
        PaymentData paymentData,
        BigDecimal total) {
}
