package com.dev.models;



public class Local {
    private String nom;
    private int capacite;

    public Local(String nom, int capacite) {
        this.nom = nom;
        this.capacite = capacite;
    }

    // Getters
    public String getNom() { return nom; }
    public int getCapacite() { return capacite; }
}