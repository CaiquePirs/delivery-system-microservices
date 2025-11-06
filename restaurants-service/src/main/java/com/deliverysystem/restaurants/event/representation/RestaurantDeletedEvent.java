package com.deliverysystem.restaurants.event.representation;

import java.util.UUID;

public record RestaurantDeletedEvent(
        UUID restaurantId,
        String email,
        String status) {
}
