package com.app.family.exceptions;

import org.springframework.http.HttpStatus;
import com.app.BaseException;

public class AlreadyMemberException extends BaseException {
    public AlreadyMemberException(String familyId) {
        super("You are already a member of this family with invite code " + familyId, HttpStatus.BAD_REQUEST);
    }
}
