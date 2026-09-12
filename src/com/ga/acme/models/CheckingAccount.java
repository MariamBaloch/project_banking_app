package com.ga.acme.models;

public class CheckingAccount extends Account {
    public CheckingAccount() {
    }

    public CheckingAccount(String userId, Mastercard mastercard, MastercardPlatinum mastercardPlatinum, MastercardTitanium mastercardTitanium) {
        super(userId, mastercard, mastercardPlatinum, mastercardTitanium);
    }
}