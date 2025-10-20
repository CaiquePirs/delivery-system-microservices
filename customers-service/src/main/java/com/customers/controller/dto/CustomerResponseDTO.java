package com.customers.controller.dto;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record CustomerResponseDTO(
        UUID id,
        String name,
        String email,
        String phone,
        List<AddressResponseDTO> address) {
}
