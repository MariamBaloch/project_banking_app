package com.ga.acme.enums;

public enum AccountType {
    SAVINGS_ACCOUNT("Savings Account"),
    CHECKING_ACCOUNT("Checking Account");

    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}