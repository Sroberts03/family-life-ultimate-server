package com.app.family.exceptions;

public class OwnerMustBeAdultException extends Exception {
    public OwnerMustBeAdultException() {
        super("You must be an adult to create a family");
    }
}
