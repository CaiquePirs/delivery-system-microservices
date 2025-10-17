package com.customers.controller.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AddressResponseDTO(
       UUID id,
       String street,
       String number,
       String zipcode,
       String neighborhood,
       String city,
       String state,
       String country){
}
