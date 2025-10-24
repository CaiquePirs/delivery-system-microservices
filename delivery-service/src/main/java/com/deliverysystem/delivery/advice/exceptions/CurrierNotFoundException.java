package com.deliverysystem.delivery.advice.exceptions;

public class CurrierNotFoundException extends RuntimeException {
    public CurrierNotFoundException(String message) {
        super(message);
    }
}
