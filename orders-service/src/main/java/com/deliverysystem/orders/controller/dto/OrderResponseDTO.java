package com.deliverysystem.orders.controller.dto;

import com.deliverysystem.orders.event.representation.CustomerResponseEvent;
import com.deliverysystem.orders.model.ItemsOrder;
import com.deliverysystem.orders.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderResponseDTO {

    private String id;
    private UUID restaurantId;
    private String restaurantEmail;
    private LocalDate orderDate;
    private BigDecimal total;
    private OrderStatus status;
    private String notes;
    private LocalDateTime estimated_delivery;
    private List<ItemsOrder> items;
    private CustomerResponseEvent customer;
}
