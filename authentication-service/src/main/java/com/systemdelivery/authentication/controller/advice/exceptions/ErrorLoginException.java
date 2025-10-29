package com.systemdelivery.authentication.controller.advice.exceptions;

public class ErrorLoginException extends RuntimeException {
    public ErrorLoginException(String message) {
        super(message);
    }
}
