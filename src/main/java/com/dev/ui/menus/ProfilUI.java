package com.dev.ui.menus;


import com.dev.dao.UtilisateurDAO;
import com.dev.models.Utilisateur;

import javax.swing.*;
import java.awt.*;

public class ProfilUI extends JDialog {
    private final Utilisateur utilisateur;
    private final UtilisateurDAO utilisateurDAO;

    private JPasswordField ancienMotDePasseField;
    private JPasswordField nouveauMotDePasseField;
    private JPasswordField confirmationMotDePasseField;

    public ProfilUI(JFrame parent, Utilisateur utilisateur) {
        super(parent, "Mon Profil", true);
        this.utilisateur = utilisateur;
        this.utilisateurDAO = new UtilisateurDAO();

        // Configuration de la fenêtre
        setSize(400, 300);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(parent);

        // Panneau principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Informations de base
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(new JLabel("Informations du Profil", SwingConstants.CENTER), gbc);

        // Nom d'utilisateur
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Nom d'utilisateur : "), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(utilisateur.getUsername()), gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Email : "), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(utilisateur.getEmail()), gbc);

        // Changement de mot de passe
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        mainPanel.add(new JSeparator(), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Ancien mot de passe : "), gbc);
        gbc.gridx = 1;
        ancienMotDePasseField = new JPasswordField(20);
        mainPanel.add(ancienMotDePasseField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Nouveau mot de passe : "), gbc);
        gbc.gridx = 1;
        nouveauMotDePasseField = new JPasswordField(20);
        mainPanel.add(nouveauMotDePasseField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Confirmer le mot de passe : "), gbc);
        gbc.gridx = 1;
        confirmationMotDePasseField = new JPasswordField(20);
        mainPanel.add(confirmationMotDePasseField, gbc);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton changerMotDePasseButton = new JButton("Changer le mot de passe");
        changerMotDePasseButton.addActionListener(e -> changerMotDePasse());
        JButton annulerButton = new JButton("Annuler");
        annulerButton.addActionListener(e -> dispose());

        buttonPanel.add(changerMotDePasseButton);
        buttonPanel.add(annulerButton);

        // Assemblage
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void changerMotDePasse() {
        String ancienMotDePasse = new String(ancienMotDePasseField.getPassword());
        String nouveauMotDePasse = new String(nouveauMotDePasseField.getPassword());
        String confirmationMotDePasse = new String(confirmationMotDePasseField.getPassword());

        // Validation
        if (ancienMotDePasse.isEmpty() || nouveauMotDePasse.isEmpty() || confirmationMotDePasse.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Tous les champs doivent être remplis",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Vérifier la correspondance des nouveaux mots de passe
        if (!nouveauMotDePasse.equals(confirmationMotDePasse)) {
            JOptionPane.showMessageDialog(this,
                    "Les nouveaux mots de passe ne correspondent pas",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Vérifier l'ancien mot de passe
        if (!utilisateurDAO.verifierMotDePasse(utilisateur.getUsername(), ancienMotDePasse)) {
            JOptionPane.showMessageDialog(this,
                    "L'ancien mot de passe est incorrect",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Changer le mot de passe
        try {
            utilisateurDAO.changerMotDePasse(utilisateur.getUsername(), nouveauMotDePasse);

            JOptionPane.showMessageDialog(this,
                    "Mot de passe modifié avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la modification du mot de passe : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}