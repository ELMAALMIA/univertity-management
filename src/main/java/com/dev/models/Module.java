package com.dev.models;



public class Module {
    private String nom;
    private int semestre;

    public Module(String nom, int semestre) {
        this.nom = nom;
        this.semestre = semestre;
    }


    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setSemestre(int semestre) {
        this.semestre = semestre;
    }

    // Getters
    public String getNom() { return nom; }
    public int getSemestre() { return semestre; }
}