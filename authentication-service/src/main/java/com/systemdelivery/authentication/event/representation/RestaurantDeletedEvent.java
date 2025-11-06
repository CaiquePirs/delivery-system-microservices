package com.systemdelivery.authentication.event.representation;

import java.util.UUID;

public record RestaurantDeletedEvent(
        UUID restaurantId,
        String email,
        String status) {
}
