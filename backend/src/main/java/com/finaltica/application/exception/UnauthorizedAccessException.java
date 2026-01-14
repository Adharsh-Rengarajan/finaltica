package com.finaltica.application.exception;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException() {
        super("You don't have permission to access this resource");
    }
    
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}