package com.ga.acme.enums;

public enum DateFilters {
    TODAY("Today"),
    YESTERDAY("Yesterday"),
    LAST_WEEK("Last Week"),
    LAST_7_DAYS("Last 7 Days"),
    LAST_30_DAYS("Last 30 Days"),
    LAST_MONTH("Last Month"),
    ALL_HISTORY("All History");

    private final String displayName;

    DateFilters(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}