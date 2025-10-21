package com.deliverysystem.orders.controller.dto;

import com.deliverysystem.orders.model.ItemsOrder;
import com.deliverysystem.orders.model.PaymentData;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderEventPublisherDTO(
        String id,
        UUID restaurantId,
        LocalDate orderDate,
        BigDecimal total,
        String status,
        String notes,
        PaymentData paymentData,
        LocalDateTime estimated_delivery,
        List<ItemsOrder> items,
        CustomerEventPublisherDTO customer) {
}
