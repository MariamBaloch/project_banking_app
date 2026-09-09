package com.ga.acme.util;

public enum FilePath {
    USERS("Data/users.txt");

    private final String path;

    FilePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}