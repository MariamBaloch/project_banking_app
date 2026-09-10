package com.ga.acme.models;

public class SavingsAccount extends Account {
    public SavingsAccount() {
    }

    public SavingsAccount(String id, String userId, Boolean mastercard, Boolean mastercardPlatinum, Boolean mastercardTitanium) {
        super(id, userId, mastercard, mastercardPlatinum, mastercardTitanium);
    }
}
