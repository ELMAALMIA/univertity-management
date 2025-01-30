package com.dev.enums;

public enum SessionType {
    NORMALE("Session Normale"),
    RATTRAPAGE("Session de Rattrapage");

    private final String displayName;

    SessionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}