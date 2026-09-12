package com.ga.acme.models;

public class SavingsAccount extends Account {
    public SavingsAccount() {
    }

    public SavingsAccount(String userId, Mastercard mastercard, MastercardPlatinum mastercardPlatinum, MastercardTitanium mastercardTitanium) {
        super(userId, mastercard, mastercardPlatinum, mastercardTitanium);
    }
}