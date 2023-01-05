package com.java8.tms.role.service;

public class RoleNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    public RoleNotFoundException(String roleName, String message) {
        super(String.format("Failed for [%s]: %s", roleName, message));
    }
}
