package com.ga.acme.models;

public class CheckingAccount extends Account {
    public CheckingAccount() {
    }

    public CheckingAccount(String id, String userId, Boolean mastercard, Boolean mastercardPlatinum, Boolean mastercardTitanium) {
        super(id, userId, mastercard, mastercardPlatinum, mastercardTitanium);
    }
}
