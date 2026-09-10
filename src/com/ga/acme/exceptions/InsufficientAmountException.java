package com.ga.acme.exceptions;

public class InsufficientAmountException extends Exception {
    public InsufficientAmountException(String message) {
        super(message);
    }
}