package com.mftplus.spring_security.home.exception;

public class HomeException extends RuntimeException {

    private final String errorCode;

    public HomeException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

}
