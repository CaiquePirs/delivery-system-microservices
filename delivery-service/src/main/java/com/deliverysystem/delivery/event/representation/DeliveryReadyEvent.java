package com.deliverysystem.delivery.event.representation;

import java.util.UUID;

public record DeliveryReadyEvent(
        UUID deliveryId,
        String orderId
) {
}
