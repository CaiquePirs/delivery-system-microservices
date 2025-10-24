package com.deliverysystem.delivery.controller.dtos;

import com.deliverysystem.delivery.model.enums.VehicleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CurrierRequestDTO(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Emails should be valid")
        @Size(max = 150, message = "Email must be at most 150 characters")
        String email,

        @NotBlank(message = "Phone number is required")
        @Size(min = 8, max = 20, message = "Phone number must be between 8 and 20 characters")
        String phone,

        @NotNull(message = "Vehicle type is required")
        VehicleType vehicleType
) {
}
