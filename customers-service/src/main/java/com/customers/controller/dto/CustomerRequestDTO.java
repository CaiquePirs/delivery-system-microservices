package com.customers.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CustomerRequestDTO(
        @NotBlank(message = "Name is required")
        String name,

        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Phone is required")
        String phone,

        @NotNull(message = "Address is required")
        List<AddressRequestDTO> addressRequest) {
}
