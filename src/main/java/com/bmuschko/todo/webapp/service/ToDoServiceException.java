package com.bmuschko.todo.webapp.service;

public class ToDoServiceException extends RuntimeException {

    public ToDoServiceException(String message) {
        super(message);
    }

    public ToDoServiceException(Throwable cause) {
        super(cause);
    }
}
