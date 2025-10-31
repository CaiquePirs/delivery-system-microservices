package com.systemdelivery.authentication.controller.dto;

public record CreateCustomerRequestDTO(
        String name,
        String email,
        String phone,
        CreateAddressRequestDTO address) {
}
