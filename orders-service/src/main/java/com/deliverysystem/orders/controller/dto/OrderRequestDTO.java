package com.deliverysystem.orders.controller.dto;

import com.deliverysystem.orders.model.PaymentData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record OrderRequestDTO(
        @NotNull(message = "Customer ID is required")
        UUID customerId,

        @NotNull(message = "Delivery Address ID is required")
        UUID deliveryAddressId,

        @NotNull(message = "Restaurant ID is required")
        UUID restaurantId,

        String notes,

        @NotNull(message = "PaymentData ID is required")
        PaymentData paymentData,

        @NotNull(message = "Items Order is required")
        @Valid
        List<ItemOrderRequestDTO> itemsDTO) {
}
