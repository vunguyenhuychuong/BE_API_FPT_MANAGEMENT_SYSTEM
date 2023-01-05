package com.java8.tms.role.customException;

import java.util.UUID;

public class RoleIdNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public RoleIdNotFoundException(UUID roleId, String message) {
        super(String.format("Failed for Role [%s]: %s", roleId, message));
    }
}
