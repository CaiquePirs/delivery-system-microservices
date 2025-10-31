package com.systemdelivery.authentication.controller.dto;

public record CreateCustomerRequestDTO(
        String name,
        String email,
        String password,
        String phone,
        CreateAddressRequestDTO address) {
}
