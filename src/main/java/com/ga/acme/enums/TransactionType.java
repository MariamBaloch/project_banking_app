package com.ga.acme.enums;

public enum TransactionType {
    DEPOSIT("Deposit to another account"),
    DEPOSIT_TO_OWN_ACCOUNT("Deposit to own account"),
    WITHDRAWAL("Withdrawal"),
    TRANSFER("Transfer to another account"),
    TRANSFER_TO_OWN_ACCOUNT("Transfer to own account"),
    OVERDRAFT_RESOLUTION("Overdraft Resolution");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
