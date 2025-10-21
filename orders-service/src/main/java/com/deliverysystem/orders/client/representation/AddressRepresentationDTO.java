package com.deliverysystem.orders.client.representation;

import java.util.UUID;

public record AddressRepresentationDTO(
        UUID id,
        String street,
        String number,
        String zipcode,
        String neighborhood,
        String city,
        String state,
        String country) {
}
