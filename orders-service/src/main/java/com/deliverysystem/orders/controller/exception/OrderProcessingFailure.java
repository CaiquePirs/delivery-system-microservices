package com.deliverysystem.orders.controller.exception;

public class OrderProcessingFailure extends RuntimeException {
    public OrderProcessingFailure(String message) {
        super(message);
    }
}
