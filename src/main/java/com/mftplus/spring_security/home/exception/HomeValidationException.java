package com.mftplus.spring_security.home.exception;

public class HomeValidationException extends HomeException {

    private final String field;

    public HomeValidationException(String field, String message) {
        super("HOME_VALIDATION_ERROR", message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}