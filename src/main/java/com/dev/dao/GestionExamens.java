package com.dev.dao;

import com.dev.models.Examen;

import java.util.ArrayList;
import java.util.List;

public class GestionExamens {
    private List<Examen> examens;

    public GestionExamens() {
        this.examens = new ArrayList<>();
    }

    // Ajouter un examen
    public void ajouterExamen(Examen examen) {
        examens.add(examen);
    }

    // Supprimer un examen
    public void supprimerExamen(Examen examen) {
        examens.remove(examen);
    }

    // Getter
    public List<Examen> getExamens() { return examens; }
}