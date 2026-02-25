package com.mftplus.spring_security.home.exception;

import com.mftplus.spring_security.home.model.enums.HomeStatus;

public class HomeNotAvailableException extends HomeException {
    public HomeNotAvailableException(Long id, HomeStatus currentStatus) {
        super("HOME_NOT_AVAILABLE",
                "Home with id " + id + " is not available. Current status: " + currentStatus);
    }
}