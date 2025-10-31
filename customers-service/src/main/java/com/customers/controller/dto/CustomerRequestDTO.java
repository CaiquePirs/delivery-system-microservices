package com.customers.controller.dto;

public record CustomerRequestDTO(
        String name,
        String email,
        String phone,
        AddressRequestDTO address) {
}
