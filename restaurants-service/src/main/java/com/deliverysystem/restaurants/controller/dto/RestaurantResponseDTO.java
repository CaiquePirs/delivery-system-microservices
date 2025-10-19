package com.deliverysystem.restaurants.controller.dto;

import com.deliverysystem.restaurants.model.Address;

import java.util.List;
import java.util.UUID;

public record RestaurantResponseDTO(
        UUID id,
        String name,
        String email,
        String website,
        String description,
        Address address,
        List<MenuResponseDTO> menus) {
}