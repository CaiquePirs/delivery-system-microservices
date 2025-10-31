package com.systemdelivery.authentication.controller.dto;

public record CreateAddressRequestDTO(
        String street,
        String number,
        String zipcode,
        String neighborhood,
        String city,
        String state,
        String country) {
}
