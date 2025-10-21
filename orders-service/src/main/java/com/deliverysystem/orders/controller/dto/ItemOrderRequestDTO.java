package com.deliverysystem.orders.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ItemOrderRequestDTO(

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "The minimum order quantity is 1.")
        Integer quantity,

        @NotNull(message = "Menu ID is required")
        UUID menuId) {
}
