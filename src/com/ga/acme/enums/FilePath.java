package com.ga.acme.enums;

public enum FilePath {
    USERS("Data/users.txt"),
    ACCOUNTS("Data/accounts.txt"),
    CARDS("Data/cards.txt");

    private final String path;

    FilePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}