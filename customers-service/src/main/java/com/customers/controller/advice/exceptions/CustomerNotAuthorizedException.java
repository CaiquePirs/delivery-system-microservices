package com.customers.controller.advice.exceptions;

public class CustomerNotAuthorizedException extends RuntimeException {
    public CustomerNotAuthorizedException(String message) {
        super(message);
    }
}
