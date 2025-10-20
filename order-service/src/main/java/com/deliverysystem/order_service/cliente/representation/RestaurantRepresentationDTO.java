package com.deliverysystem.order_service.cliente.representation;

import java.util.List;
import java.util.UUID;

public record RestaurantRepresentationDTO(
        UUID id,
        String name,
        String email,
        String website,
        String description,
        String status,
        AddressRepresentationDTO address,
        List<MenuRepresentationDTO> menus) {
}
