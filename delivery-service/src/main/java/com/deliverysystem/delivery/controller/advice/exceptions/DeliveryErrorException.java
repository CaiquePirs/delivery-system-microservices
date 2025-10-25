package com.deliverysystem.delivery.controller.advice.exceptions;

public class DeliveryErrorException extends RuntimeException {
    public DeliveryErrorException(String message) {
        super(message);
    }
}
