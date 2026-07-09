package com.app.family.exceptions;

import com.app.BaseException;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BaseException {

    public UnauthorizedException() {
        super("You are not authorized to perform this action", HttpStatus.UNAUTHORIZED);
    }

}
