package com.jon.user_manager.util.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class NoResourceException extends RuntimeException{
    public NoResourceException(Class<?> entityClass) {
        super("No " + entityClass.getSimpleName() + " to show");
    }
}
