package com.deliveysistem.notification.event.representation;

import java.util.UUID;

public record AddressEventDTO(
        UUID id,
        String street,
        String number,
        String zipcode,
        String neighborhood,
        String city,
        String state,
        String country) {
}
