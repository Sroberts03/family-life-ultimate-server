package com.app.chore.exceptions;

import com.app.BaseException;
import org.springframework.http.HttpStatus;

public class ChoreNotFoundException extends BaseException {

    public ChoreNotFoundException(int choreId) {
        super("Chore with ID " + choreId + " not found", HttpStatus.NOT_FOUND);
    }
    
}
