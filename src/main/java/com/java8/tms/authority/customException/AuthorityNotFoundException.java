package com.java8.tms.authority.customException;

import java.util.UUID;

public class AuthorityNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public AuthorityNotFoundException(UUID authorityId, String message) {
        super(String.format("Failed for Authority [%s]: %s", authorityId, message));
    }
}
