package com.app;

import org.springframework.http.HttpStatus;

public class BaseException extends Exception {
    public HttpStatus status;

    protected BaseException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
    
}
