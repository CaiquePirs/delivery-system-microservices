package com.deliverysystem.orders.event.representation;

import com.deliverysystem.orders.client.representation.AddressRepresentationDTO;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CustomerEventPublisherDTO(
        UUID id,
        String name,
        String email,
        String phone,
        AddressRepresentationDTO deliveryAddress) {
}
