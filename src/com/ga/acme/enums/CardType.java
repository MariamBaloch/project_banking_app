package com.ga.acme.enums;

public enum CardType {
    MASTERCARD("Mastercard"),
    MASTERCARDPLATINUM("Mastercard Platinum"),
    MASTERCARDTITANIUM("Mastercard Titanium");

    private final String displayName;

    CardType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}