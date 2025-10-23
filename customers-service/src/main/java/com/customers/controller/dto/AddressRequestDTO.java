package com.customers.controller.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record AddressRequestDTO(
        @NotBlank(message = "Customer ID is required")
        UUID customerId,

        @NotBlank(message = "Street is required")
        String street,

        @NotBlank(message = "Number is required")
        String number,

        @NotBlank(message = "ZipCode is required")
        String zipcode,

        @NotBlank(message = "Neighborhood is required")
        String neighborhood,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "State is required")
        String state,

        @NotBlank(message = "Country is required")
        String country) {
}
