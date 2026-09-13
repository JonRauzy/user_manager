package com.jon.user_manager.util.exceptionHandler;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException() {
        super("Invalid credentials");
    }
}
