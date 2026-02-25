package com.mftplus.spring_security.home.exception;

public class HomeOwnerNotFoundException extends HomeException {
    public HomeOwnerNotFoundException(Long personId) {
        super("HOME_OWNER_NOT_FOUND", "Owner (Person) not found with id: " + personId);
    }
}