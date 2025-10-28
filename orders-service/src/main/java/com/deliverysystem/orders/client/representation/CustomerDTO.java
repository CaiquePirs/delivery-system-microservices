package com.deliverysystem.orders.client.representation;

import java.util.List;
import java.util.UUID;

public record CustomerDTO(
        UUID id,
        String name,
        String email,
        String phone,
        List<DeliveryAddressDTO> address) {
}
