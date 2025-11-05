package com.deliverysystem.restaurants.controller.dto;

import com.deliverysystem.restaurants.model.Address;

public record RestaurantRequestDTO(
        String email,
        String name,
        String website,
        String description,
        Address address) {
}
