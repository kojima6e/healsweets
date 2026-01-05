package com.healsweets.entity;

public enum OrderStatus {
    PENDING("処理中"),
    CONFIRMED("確認済み"),
    SHIPPED("発送済み"),
    DELIVERED("配達完了"),
    CANCELLED("キャンセル");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
