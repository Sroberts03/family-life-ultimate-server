package com.app.family.exceptions;

import com.app.BaseException;
import org.springframework.http.HttpStatus;

public class MemberNotFoundException extends BaseException {

    public MemberNotFoundException(String memberId, String familyId) {
        super("Member with ID " + memberId + " not found in family with ID " + familyId, HttpStatus.NOT_FOUND);
    }
}
