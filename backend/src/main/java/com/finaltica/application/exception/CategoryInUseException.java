package com.finaltica.application.exception;

public class CategoryInUseException extends RuntimeException {
    public CategoryInUseException() {
        super("Cannot delete category with existing transactions");
    }
    
    public CategoryInUseException(String message) {
        super(message);
    }
}