package com.dev.models;

public class Module {
    private Integer id;
    private String nom;
    private Integer semestre;
    private Integer filiereId;

    // Constructeur complet
    public Module(Integer id, String nom, Integer semestre, Integer filiereId) {
        this.id = id;
        this.nom = nom;
        this.semestre = semestre;
        this.filiereId = filiereId;
    }

    // Constructeur pour nouveau module
    public Module(String nom, Integer semestre, Integer filiereId) {
        this.nom = nom;
        this.semestre = semestre;
        this.filiereId = filiereId;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public Integer getSemestre() {
        return semestre;
    }

    public Integer getFiliereId() {
        return filiereId;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setSemestre(Integer semestre) {
        this.semestre = semestre;
    }

    public void setFiliereId(Integer filiereId) {
        this.filiereId = filiereId;
    }

    @Override
    public String toString() {
        return nom + " (S" + semestre + ")";
    }
}