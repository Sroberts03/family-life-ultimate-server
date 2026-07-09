package com.app.family.exceptions;
import org.springframework.http.HttpStatus;

import com.app.BaseException;

public class FamilyNotFoundException extends BaseException {
    public FamilyNotFoundException(String familyId) {
        super("Family not found with invite code " + familyId, HttpStatus.NOT_FOUND);
    }
}
