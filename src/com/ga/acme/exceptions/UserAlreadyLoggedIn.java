package com.ga.acme.exceptions;

public class UserAlreadyLoggedIn extends Exception {
    public UserAlreadyLoggedIn() {
        super("The user is already logged in");
    }
}
