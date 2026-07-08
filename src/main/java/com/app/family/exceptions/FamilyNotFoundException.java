package com.app.family.exceptions;

public class FamilyNotFoundException extends Exception {
    public FamilyNotFoundException(String familyId) {
        super("Family not found with invite code " + familyId);
    }
}
