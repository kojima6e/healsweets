package com.healsweets.entity;

public enum ContactStatus {
    NEW("新規"),
    IN_PROGRESS("対応中"),
    RESOLVED("解決済み");

    private final String displayName;

    ContactStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
