package com.ga.acme.exceptions;

public class AccountTypeNotSupportedException extends Exception {
    public AccountTypeNotSupportedException() {
        super("Account Type Not Supported, Enter either savingsaccount or checkingaccount");
    }
}
