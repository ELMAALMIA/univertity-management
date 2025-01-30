package com.dev.models;



public class Local {
    private  int id;
    private String nom;
    private int capacite;

    public Local(String nom, int capacite) {
        this.nom = nom;
        this.capacite = capacite;
    }


    public Local(int id,String nom, int capacite) {
        this.nom = nom;
        this.id = id;
        this.capacite = capacite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getters
    public String getNom() { return nom; }
    public int getCapacite() { return capacite; }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }
}