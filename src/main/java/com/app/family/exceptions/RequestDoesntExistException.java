package com.app.family.exceptions;

import org.springframework.http.HttpStatus;

import com.app.BaseException;

public class RequestDoesntExistException extends BaseException {

    public RequestDoesntExistException(int requestId) {
        super(String.format("Request %d does not exist", requestId), HttpStatus.NOT_FOUND);
    }
}
