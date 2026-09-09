package com.ga.acme.exceptions;

public class UserAlreadyLoggedIn extends Exception {
    public UserAlreadyLoggedIn(String message) {
        super(message);
    }
}
