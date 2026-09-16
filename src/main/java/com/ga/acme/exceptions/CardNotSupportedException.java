package com.ga.acme.exceptions;

public class CardNotSupportedException extends Exception {
    public CardNotSupportedException() {
        super("This card is not supported by the account");
    }
}
