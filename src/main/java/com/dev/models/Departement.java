package com.dev.models;


import java.util.ArrayList;
import java.util.List;

public class Departement {
    private int id;
    private String nom;
    private List<Filiere> filieres;

    public Departement(String nom) {
        this.nom = nom;
        this.filieres = new ArrayList<>();
    }

    // Ajouter une filière
    public void ajouterFiliere(Filiere filiere) {
        filieres.add(filiere);
    }

    public int getId() {
        return id;
    }

    public void setId(int idDepartement) {
        this.id = idDepartement;
    }

    public void setFilieres(List<Filiere> filieres) {
        this.filieres = filieres;
    }

    // Getters
    public String getNom() { return nom; }
    public List<Filiere> getFilieres() { return filieres; }
}