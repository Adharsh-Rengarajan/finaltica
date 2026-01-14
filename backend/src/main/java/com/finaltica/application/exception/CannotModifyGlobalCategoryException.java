package com.finaltica.application.exception;

public class CannotModifyGlobalCategoryException extends RuntimeException {
    public CannotModifyGlobalCategoryException() {
        super("Cannot modify global categories");
    }
    
    public CannotModifyGlobalCategoryException(String message) {
        super(message);
    }
}