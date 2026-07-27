package com.jon.user_manager.util.exceptionHandler;

public class ResourceExist extends RuntimeException {
    public ResourceExist(Class<?> entityClass) {
        super("This " + entityClass.getSimpleName() + " already exist");
    }
}
