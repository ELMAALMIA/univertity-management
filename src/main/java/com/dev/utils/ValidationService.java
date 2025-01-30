package com.dev.utils;



import com.dev.models.Surveillant;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ValidationService {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public static class ValidationResult {
        private final List<String> errors = new ArrayList<>();

        public void addError(String error) {
            errors.add(error);
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public List<String> getErrors() {
            return errors;
        }
    }

    public static ValidationResult validateSurveillant(Surveillant surveillant) {
        ValidationResult result = new ValidationResult();

        if (surveillant.getNom() == null || surveillant.getNom().trim().isEmpty()) {
            result.addError("Le nom est obligatoire");
        }

        if (surveillant.getPrenom() == null || surveillant.getPrenom().trim().isEmpty()) {
            result.addError("Le prénom est obligatoire");
        }

        if (surveillant.getDepartementId()==0) {
            result.addError("Le département est obligatoire");
        }

        return result;
    }

//    public static ValidationResult validateExamen(Examen examen) {
//        ValidationResult result = new ValidationResult();
//
//        if (examen.getModule() == null) {
//            result.addError("Le module est obligatoire");
//        }
//
//        if (examen.getDate() == null) {
//            result.addError("La date est obligatoire");
//        }
//
//        if (examen.getHeureDebut() == null || examen.getHeureDebut().trim().isEmpty()) {
//            result.addError("L'heure de début est obligatoire");
//        }
//
//        if (examen.getHeureFin() == null || examen.getHeureFin().trim().isEmpty()) {
//            result.addError("L'heure de fin est obligatoire");
//        }
//
//        if (examen.getLocaux().isEmpty()) {
//            result.addError("Au moins un local doit être assigné");
//        }
//
//        return result;
//    }
}