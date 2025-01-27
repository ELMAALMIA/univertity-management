package com.dev.enums;

public enum Role {
    ADMIN("Administrateur"),
    CHEF_DEPT("Chef de département"),
    SURVEILLANT("Surveillant");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }


    public String getDisplayName() {
        return displayName;
    }
}