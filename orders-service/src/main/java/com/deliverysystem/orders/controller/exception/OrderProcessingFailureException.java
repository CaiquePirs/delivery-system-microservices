package com.deliverysystem.orders.controller.exception;

public class OrderProcessingFailureException extends RuntimeException {
    public OrderProcessingFailureException(String message) {
        super(message);
    }
}
