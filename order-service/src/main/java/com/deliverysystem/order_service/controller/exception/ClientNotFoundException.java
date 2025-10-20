package com.deliverysystem.order_service.controller.exception;

public class ClientNotFoundException extends RuntimeException {
  public ClientNotFoundException(String message) {
    super(message);
  }
}
