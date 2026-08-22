package com.jon.user_manager.util.exceptionHandler;

public class BadToken extends RuntimeException {
    public BadToken() {
        super("Empty or Malformed token - Login and come back");
    }
}
