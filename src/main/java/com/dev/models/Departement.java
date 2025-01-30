package com.dev.models;

import java.util.ArrayList;
import java.util.List;

public class Departement {
    private Integer id;
    private String nom;
    private List<Filiere> filieres;

    // Constructeur pour un nouveau département
    public Departement(String nom) {
        this.nom = nom;
        this.filieres = new ArrayList<>();
    }

    // Constructeur pour un département existant (venant de la BD)
    public Departement(Integer id, String nom) {
        this.id = id;
        this.nom = nom;
        this.filieres = new ArrayList<>();
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public List<Filiere> getFilieres() {
        return filieres;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setFilieres(List<Filiere> filieres) {
        this.filieres = filieres;
    }

    // Méthodes utilitaires
    public void ajouterFiliere(Filiere filiere) {
        if (filiere != null) {
            filieres.add(filiere);
            filiere.setDepartementId(this.id);
        }
    }

    @Override
    public String toString() {
        return nom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Departement that = (Departement) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}