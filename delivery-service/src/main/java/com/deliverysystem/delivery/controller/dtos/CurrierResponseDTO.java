package com.deliverysystem.delivery.controller.dtos;

import com.deliverysystem.delivery.model.enums.VehicleType;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CurrierResponseDTO(
        UUID id,
        String name,
        String email,
        String phone,
        VehicleType vehicleType) {
}
