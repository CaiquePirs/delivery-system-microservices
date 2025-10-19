package com.deliverysystem.restaurants.controller.dto;

import com.deliverysystem.restaurants.model.enums.MenuType;

import java.math.BigDecimal;
import java.util.UUID;

public record MenuResponseDTO(
        UUID id,
        String description,
        BigDecimal price,
        MenuType menuType) {
}
