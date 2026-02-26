package com.mftplus.spring_security.home.exception;

public class HomeOwnerNotFoundException extends HomeException {
    public HomeOwnerNotFoundException(Long userId) { // تغییر نام پارامتر
        super("HOME_OWNER_NOT_FOUND", "Owner (User) not found with id: " + userId); // تغییر پیام
    }
}