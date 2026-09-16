package com.ga.acme.enums;

public enum FilePath {
    USERS("data/users.txt"),
    ACCOUNTS("data/accounts.txt"),
    CARDS("data/cards.txt"),
    CUSTOMER_TRANSACTIONS("data/Customer-");

    private final String path;

    FilePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}