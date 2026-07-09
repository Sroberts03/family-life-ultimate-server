package com.app.family.exceptions;

import org.springframework.http.HttpStatus;
import com.app.BaseException;

public class OwnerMustBeAdultException extends BaseException {
    public OwnerMustBeAdultException() {
        super("You must be an adult to create a family", HttpStatus.BAD_REQUEST);
    }
}
