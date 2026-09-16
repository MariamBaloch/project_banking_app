package com.ga.acme.exceptions;

public class AccountNotLoggedInException extends Exception {
    public AccountNotLoggedInException() {
        super("Account not logged in. Please login first.");
    }
}
