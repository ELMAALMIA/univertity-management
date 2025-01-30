package com.dev.enums;

public enum TypeSurveillant {
    ENSEIGNANT("Enseignant"),
    ADMINISTRATIF("Attaché Administratif");

    private final String description;

    // Constructeur de l'énumération
    TypeSurveillant(String description) {
        this.description = description;
    }


    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description; // Affiche la description au lieu du nom de l'énumération
    }
}
