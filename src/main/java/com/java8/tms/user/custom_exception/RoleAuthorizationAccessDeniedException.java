package com.java8.tms.user.custom_exception;

public class RoleAuthorizationAccessDeniedException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public RoleAuthorizationAccessDeniedException(String action, String message) {
        super(String.format("%s", message));
    }
}
