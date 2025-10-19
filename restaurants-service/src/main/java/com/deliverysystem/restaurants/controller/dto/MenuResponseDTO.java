package com.deliverysystem.restaurants.controller.dto;

import com.deliverysystem.restaurants.model.enums.MenuType;

import java.math.BigDecimal;

public record MenuResponseDTO(
        String description,
        BigDecimal price,
        MenuType menuType) {
}
