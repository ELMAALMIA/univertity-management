package com.dev.models;


// Modèle représentant l'affectation d'un surveillant à un examen
public class AffectationSurveillant {
    private int id;  // Identifiant unique de l'affectation
    private int examenId;  // ID de l'examen
    private int surveillantId;  // ID du surveillant

    // Constructeur sans ID (pour la création)
    public AffectationSurveillant(int examenId, int surveillantId) {
        this.examenId = examenId;
        this.surveillantId = surveillantId;
    }

    // Constructeur avec ID (pour les objets récupérés de la base de données)
    public AffectationSurveillant(int id, int examenId, int surveillantId) {
        this.id = id;
        this.examenId = examenId;
        this.surveillantId = surveillantId;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getExamenId() {
        return examenId;
    }

    public void setExamenId(int examenId) {
        this.examenId = examenId;
    }

    public int getSurveillantId() {
        return surveillantId;
    }

    public void setSurveillantId(int surveillantId) {
        this.surveillantId = surveillantId;
    }

    @Override
    public String toString() {
        return "AffectationSurveillant{" +
                "id=" + id +
                ", examenId=" + examenId +
                ", surveillantId=" + surveillantId +
                '}';
    }
}
