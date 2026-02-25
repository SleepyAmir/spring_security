package com.mftplus.spring_security.home.exception;

public class HomeAlreadySoldException extends HomeException {
    public HomeAlreadySoldException(Long id) {
        super("HOME_ALREADY_SOLD", "Home with id " + id + " is already sold.");
    }
}