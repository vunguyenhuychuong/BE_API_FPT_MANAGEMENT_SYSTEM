package com.java8.tms.user.custom_exception;

public class OtpNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public OtpNotFoundException() {
        super("OTP not found");
    }
}
