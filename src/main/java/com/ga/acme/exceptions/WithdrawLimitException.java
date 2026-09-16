package com.ga.acme.exceptions;

public class WithdrawLimitException extends Exception {
    public WithdrawLimitException(String message) {
        super(message);
    }
}
