package com.deliverysystem.orders.client.representation;

import java.util.List;
import java.util.UUID;

public record RestaurantDTO(
        UUID id,
        String name,
        String email,
        String website,
        String description,
        String status,
        DeliveryAddressDTO address,
        List<MenuRepresentationDTO> menus) {
}
