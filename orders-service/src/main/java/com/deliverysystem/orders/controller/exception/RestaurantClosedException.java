package com.deliverysystem.orders.controller.exception;

public class RestaurantClosedException extends RuntimeException {
    public RestaurantClosedException(String message) {
        super(message);
    }
}
