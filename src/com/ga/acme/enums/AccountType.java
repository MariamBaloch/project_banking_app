package com.ga.acme.enums;

public enum AccountType {
    SAVINGSACCOUNT("Savings Account"),
    CHECKINGACCOUNT("Checking Account");

    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
