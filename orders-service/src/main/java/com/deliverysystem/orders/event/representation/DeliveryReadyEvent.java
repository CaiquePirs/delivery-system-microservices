package com.deliverysystem.orders.event.representation;

import java.util.UUID;

public record DeliveryReadyEvent(
        UUID deliveryId,
        String orderId) {
}
