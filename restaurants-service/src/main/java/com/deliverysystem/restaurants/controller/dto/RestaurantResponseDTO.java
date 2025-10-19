package com.deliverysystem.restaurants.controller.dto;

import com.deliverysystem.restaurants.model.Address;
import com.deliverysystem.restaurants.model.RestaurantStatus;

import java.util.List;
import java.util.UUID;

public record RestaurantResponseDTO(
        UUID id,
        String name,
        String email,
        String website,
        String description,
        RestaurantStatus status,
        Address address,
        List<MenuResponseDTO> menus) {
}