package com.java8.tms.fsu.service.impl;

public class FsuNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    public FsuNotFoundException(String FsuId, String message) {
        super(String.format("Failed for [%s]: %s", FsuId, message));
    }
}
