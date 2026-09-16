package com.ga.acme.enums;

public enum CardType {
    MASTERCARD("Mastercard"),
    MASTERCARD_PLATINUM("Mastercard Platinum"),
    MASTERCARD_TITANIUM("Mastercard Titanium");

    private final String displayName;

    CardType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}