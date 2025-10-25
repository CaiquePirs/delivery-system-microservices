package com.deliverysystem.delivery.client.representation;

import com.deliverysystem.delivery.model.Address;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CustomerRepresentationDTO(
        UUID id,
        String name,
        String email,
        String phone,
        Address deliveryAddress
) {
}
