package com.mftplus.spring_security.home.exception;

public class HomeNotFoundException extends HomeException {
    public HomeNotFoundException(Long id) {
        super("HOME_NOT_FOUND", "Home not found with id: " + id);
    }
}