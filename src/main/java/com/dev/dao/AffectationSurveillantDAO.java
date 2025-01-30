package com.dev.dao;

import com.dev.models.AffectationSurveillant;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AffectationSurveillantDAO {
    // Liste simulant une base de données
    private List<AffectationSurveillant> affectations = new ArrayList<>();

    // Récupérer toutes les affectations
    public List<AffectationSurveillant> findAll() {
        return new ArrayList<>(affectations);
    }

    // Trouver les affectations pour un surveillant spécifique
    public List<AffectationSurveillant> findBySurveillantId(int surveillantId) {
        return affectations.stream()
                .filter(a -> a.getSurveillantId() == surveillantId)
                .collect(Collectors.toList());
    }

    // Trouver les affectations pour un examen spécifique
    public List<AffectationSurveillant> findByExamenId(int examenId) {
        return affectations.stream()
                .filter(a -> a.getExamenId() == examenId)
                .collect(Collectors.toList());
    }

    // Sauvegarder une nouvelle affectation
    public AffectationSurveillant save(AffectationSurveillant affectation) {
        // Vérifier si l'affectation existe déjà
        boolean exists = affectations.stream()
                .anyMatch(a -> a.getExamenId() == affectation.getExamenId() &&
                        a.getSurveillantId() == affectation.getSurveillantId());

        if (exists) {
            throw new RuntimeException("Ce surveillant est déjà affecté à cet examen.");
        }

        // Générer un nouvel ID
        if (affectation.getId() == 0) {
            affectation.setId(affectations.size() + 1);
        }

        affectations.add(affectation);
        return affectation;
    }

    // Mettre à jour une affectation existante
    public void update(AffectationSurveillant affectation) {
        for (int i = 0; i < affectations.size(); i++) {
            if (affectations.get(i).getId() == affectation.getId()) {
                affectations.set(i, affectation);
                return;
            }
        }
        throw new RuntimeException("Affectation non trouvée");
    }

    // Supprimer une affectation
    public void delete(int id) {
        affectations.removeIf(a -> a.getId() == id);
    }
}