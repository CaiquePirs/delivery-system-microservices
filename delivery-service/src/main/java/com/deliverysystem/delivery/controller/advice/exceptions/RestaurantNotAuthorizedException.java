package com.deliverysystem.delivery.controller.advice.exceptions;

public class RestaurantNotAuthorizedException extends RuntimeException {
    public RestaurantNotAuthorizedException(String message) {
        super(message);
    }
}
