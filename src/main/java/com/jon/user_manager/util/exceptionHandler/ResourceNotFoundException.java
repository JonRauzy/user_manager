package com.jon.user_manager.util.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Long id){
        super("No user found at id : " + id);
    }

    public ResourceNotFoundException(){
        super("This user doesn't exist");
    }
}