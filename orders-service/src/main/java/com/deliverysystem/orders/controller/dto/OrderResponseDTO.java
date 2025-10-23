package com.deliverysystem.orders.controller.dto;

import com.deliverysystem.orders.event.representation.CustomerResponseDTO;
import com.deliverysystem.orders.model.ItemsOrder;
import com.deliverysystem.orders.model.enums.OrderStatus;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderResponseDTO(
        String id,
        UUID restaurantId,
        LocalDate orderDate,
        BigDecimal total,
        OrderStatus status,
        String notes,
        LocalDateTime estimated_delivery,
        List<ItemsOrder> items,
        CustomerResponseDTO customer) {
}
