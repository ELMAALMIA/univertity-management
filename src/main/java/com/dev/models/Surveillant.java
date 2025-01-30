package com.dev.models;

import com.dev.enums.TypeSurveillant;

public class Surveillant {
    private int id;
    private String nom;
    private String prenom;
    private TypeSurveillant type; // Utilisation de l'énumération
    private int departementId; // Identifiant du département

    // Constructeur sans ID (utilisé pour la création)
    public Surveillant(String nom, String prenom, TypeSurveillant type, int departementId) {
        this.nom = nom;
        this.prenom = prenom;
        this.type = type;
        this.departementId = departementId;
    }

    // Constructeur complet (avec ID, utilisé pour les objets existants)
    public Surveillant(int id, String nom, String prenom, TypeSurveillant type, int departementId) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.type = type;
        this.departementId = departementId;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public TypeSurveillant getType() {
        return type;
    }

    public void setType(TypeSurveillant type) {
        this.type = type;
    }

    public int getDepartementId() {
        return departementId;
    }

    public void setDepartementId(int departementId) {
        this.departementId = departementId;
    }

    // Représentation textuelle de l'objet
    @Override
    public String toString() {
        return nom + " " + prenom + " (" + type + ")";
    }
}
