package com.dev.models;

import java.util.ArrayList;
import java.util.List;

public class Filiere {
    private Integer id;
    private String nom;
    private Integer departementId;
    private List<Module> modules;

    // Constructeur complet pour la création depuis la base de données
    public Filiere(Integer id, String nom, Integer departementId) {
        this.id = id;
        this.nom = nom;
        this.departementId = departementId;
        this.modules = new ArrayList<>();
    }

    // Constructeur pour la création d'une nouvelle filière
    public Filiere(String nom) {
        this.nom = nom;
        this.modules = new ArrayList<>();
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public Integer getDepartementId() {
        return departementId;
    }

    public List<Module> getModules() {
        return modules;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDepartementId(Integer departementId) {
        this.departementId = departementId;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    // Méthodes utilitaires
    public void ajouterModule(Module module) {
        modules.add(module);
    }

    @Override
    public String toString() {
        return nom;
    }
}