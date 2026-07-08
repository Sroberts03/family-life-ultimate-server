package com.app.family.exceptions;

public class AlreadyMemberException extends Exception {
    public AlreadyMemberException(String familyId) {
        super("You are already a member of this family with invite code " + familyId);
    }
}
