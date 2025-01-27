package com.dev.dao;


import com.dev.models.Surveillant;

import java.util.ArrayList;
import java.util.List;

public class GestionSurveillants {
    private List<Surveillant> surveillants;

    public GestionSurveillants() {
        this.surveillants = new ArrayList<>();
    }

    // Ajouter un surveillant
    public void ajouterSurveillant(Surveillant surveillant) {
        surveillants.add(surveillant);
    }

    // Supprimer un surveillant
    public void supprimerSurveillant(Surveillant surveillant) {
        surveillants.remove(surveillant);
    }

    // Getter
    public List<Surveillant> getSurveillants() { return surveillants; }
}