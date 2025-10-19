package com.deliverysystem.restaurants.controller.advice.exceptions;

public class RestaurantFoundException extends RuntimeException {
  public RestaurantFoundException(String message) {
    super(message);
  }
}
