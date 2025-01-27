package com.dev;

import com.dev.dao.GestionSurveillants;
import com.dev.ui.LoginUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Pour s'assurer que l'interface graphique est créée dans l'Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Test de la connexion à la base de données d'abord
                com.dev.dao.DatabaseConnection.getConnection();
                System.out.println("Connexion à la base de données réussie!");

                // Créer et afficher l'interface de login
                LoginUI loginUI = new LoginUI();
                loginUI.setVisible(true); // Cette ligne est importante!

            } catch (Exception e) {
                System.err.println("Erreur lors du démarrage de l'application:");
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Erreur de connexion à la base de données: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}