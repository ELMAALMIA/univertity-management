package com.dev.models;


public class Surveillant {
    private  int id;
    private String nom;
    private String prenom;
    private String type; // Enseignant ou Attaché administratif
    private Departement departement;

    public Surveillant(String nom, String prenom, String type, String departement) {
        this.nom = nom;
        this.prenom = prenom;
        this.type = type;
        this.departement = new Departement(departement);
    }

    // Getters
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getType() { return type; }
    public Departement getDepartement() { return departement; }

    public void setId(int id) { this.id = id; }
    public int getId() { return id; }
    @Override
    public String toString() {
        return nom + " " + prenom + " (" + type + ")";
    }
}