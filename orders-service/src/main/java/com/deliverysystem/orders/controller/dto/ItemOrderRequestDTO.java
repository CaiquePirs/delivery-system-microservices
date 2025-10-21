package com.deliverysystem.orders.controller.dto;

import java.util.UUID;

public record ItemOrderRequestDTO(
        Integer quantity,
        UUID menuId) {
}
