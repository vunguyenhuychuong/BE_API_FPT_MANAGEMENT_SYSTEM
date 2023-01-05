package com.java8.tms.user.custom_exception;

public class UserNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public UserNotFoundException(String username, String message) {
        super(String.format("Failed for [%s]: %s", username, message));
    }
}
