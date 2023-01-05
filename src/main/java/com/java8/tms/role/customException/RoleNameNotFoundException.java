package com.java8.tms.role.customException;


public class RoleNameNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RoleNameNotFoundException(String name, String message) {
        super(String.format("Failed for Role [%s]: %s", name, message));
    }
}
