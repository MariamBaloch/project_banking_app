package com.ga.acme.exceptions;

public class UserAlreadyLoggedInException extends Exception {
    public UserAlreadyLoggedInException() {
        super("The user is already logged in");
    }
}
