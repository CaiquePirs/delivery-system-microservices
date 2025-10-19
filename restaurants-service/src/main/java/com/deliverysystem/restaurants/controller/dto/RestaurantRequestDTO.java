package com.deliverysystem.restaurants.controller.dto;

import com.deliverysystem.restaurants.model.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RestaurantRequestDTO(

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Website is required")
        String website,

        @NotBlank(message = "Description is required")
        String description,

        @Valid
        Address address) {
}
