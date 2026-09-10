package com.ga.acme.exceptions;

public class AccountTypeNotSupported extends Exception {
    public AccountTypeNotSupported() {
        super("Account Type Not Supported, Enter either savingsaccount or checkingaccount");
    }
}
