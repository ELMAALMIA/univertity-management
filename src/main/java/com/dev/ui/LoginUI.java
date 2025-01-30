package com.dev.ui;

import com.dev.models.Utilisateur;
import com.dev.ui.menus.MainUI;

import javax.swing.*;
import java.awt.*;

public class LoginUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private com.dev.services.AuthenticationService authService;

    public LoginUI() {
        authService = new com.dev.services.AuthenticationService();

        setTitle("Connexion");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Utilisation de GridBagLayout pour un meilleur contrôle du placement
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Panel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());

        // Username label et field
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Nom d'utilisateur:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        mainPanel.add(usernameField, gbc);

        // Password label et field
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Mot de passe:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        mainPanel.add(passwordField, gbc);

        // Bouton de connexion
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Se connecter");
        mainPanel.add(loginButton, gbc);

        // Ajouter le panel principal
        add(mainPanel);

        // Action du bouton de connexion
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try {
                Utilisateur user = authService.authenticate(username, password);
                if (user != null) {
                    new MainUI(user).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Identifiants incorrects",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur de connexion: " + ex.getMessage());
            }
        });

        // Centrer la fenêtre
        setLocationRelativeTo(null);
    }
}