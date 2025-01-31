package com.dev.enums;
public enum TypeSurveillant {
    ENSEIGNANT("Enseignant"),
    ADMINISTRATIF("Attaché Administratif");

    private final String description;

    TypeSurveillant(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    // Use this method for database operations
    public String getDatabaseValue() {
        return name(); // Returns "ENSEIGNANT" or "ADMINISTRATIF"
    }

    @Override
    public String toString() {
        return description; // Keeps the friendly description for UI display
    }
}