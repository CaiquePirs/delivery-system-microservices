package com.systemdelivery.authentication.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRestaurantRequestDTO(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        String password,

        @NotBlank(message = "Website is required")
        String website,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Address is required")
        CreateAddressRequestDTO address
) {
}
