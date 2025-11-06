package com.deliverysystem.restaurants.controller.advice.exceptions;

public class RestaurantNotAuthorizedException extends RuntimeException {
    public RestaurantNotAuthorizedException(String message) {
        super(message);
    }
}
