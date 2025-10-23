package com.deliverysystem.orders.event.representation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentApprovedEvent {
    private String id;
    private String orderId;
    private BigDecimal amount;
    private String status;
}
