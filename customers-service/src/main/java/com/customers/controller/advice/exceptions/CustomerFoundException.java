package com.customers.controller.advice.exceptions;

public class CustomerFoundException extends RuntimeException {
    public CustomerFoundException(String message) {
        super(message);
    }
}
