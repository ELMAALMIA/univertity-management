package com.dev.ui;

import com.dev.dao.GestionSurveillants;
import com.dev.enums.Role;
import com.dev.models.Utilisateur;
import javax.swing.*;
import java.awt.*;

public class MainUI extends JFrame {
    private Utilisateur currentUser;
    private JPanel contentPanel;
    private JLabel statusLabel;

    public MainUI(Utilisateur user) {
        this.currentUser = user;

        // Configuration de la fenêtre principale
        setTitle("Gestion des Surveillances d'Examens");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Création des composants principaux
        createMenuBar();
        createToolBar();
        createStatusBar();
        createContentPanel();

        // Afficher les informations de l'utilisateur
        updateStatusBar();

        // Centrer la fenêtre
        setLocationRelativeTo(null);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu Fichier
        JMenu fileMenu = createFileMenu();
        menuBar.add(fileMenu);

        // Menu Département
        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.CHEF_DEPT) {
            JMenu departementMenu = createDepartementMenu();
            menuBar.add(departementMenu);
        }

        // Menu Surveillance
        JMenu surveillanceMenu = createSurveillanceMenu();
        menuBar.add(surveillanceMenu);

        // Menu Examens
        JMenu examensMenu = createExamensMenu();
        menuBar.add(examensMenu);

        // Menu Rapports
        JMenu reportsMenu = createReportsMenu();
        menuBar.add(reportsMenu);

        setJMenuBar(menuBar);
    }

    private JMenu createFileMenu() {
        JMenu menu = new JMenu("Fichier");

        JMenuItem profile = new JMenuItem("Mon Profil");
        profile.addActionListener(e -> showProfile());

        JMenuItem settings = new JMenuItem("Paramètres");
        settings.addActionListener(e -> showSettings());

        JMenuItem logout = new JMenuItem("Déconnexion");
        logout.addActionListener(e -> logout());

        JMenuItem exit = new JMenuItem("Quitter");
        exit.addActionListener(e -> System.exit(0));

        menu.add(profile);
        menu.add(settings);
        menu.addSeparator();
        menu.add(logout);
        menu.addSeparator();
        menu.add(exit);

        return menu;
    }

    private JMenu createDepartementMenu() {
        JMenu menu = new JMenu("Département");

        JMenuItem gestionDept = new JMenuItem("Gestion des Départements");
        gestionDept.addActionListener(e -> {
            GestionDepartementsUI deptUI = new GestionDepartementsUI();
            deptUI.setVisible(true);
        });

        JMenuItem gestionFil = new JMenuItem("Gestion des Filières");
        gestionFil.addActionListener(e -> {
            GestionFilieresUI filUI = new GestionFilieresUI();
            filUI.setVisible(true);
        });

        menu.add(gestionDept);
        menu.add(gestionFil);

        return menu;
    }

    private JMenu createSurveillanceMenu() {
        JMenu menu = new JMenu("Surveillance");

        JMenuItem viewPlanning = new JMenuItem("Voir mon planning");
        viewPlanning.addActionListener(e -> showPlanning());

        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.CHEF_DEPT) {
            JMenuItem addSurveillant = new JMenuItem("Ajouter un surveillant");
            addSurveillant.addActionListener(e -> {
                AjouterSurveillantUI addSurvUI = new AjouterSurveillantUI(new GestionSurveillants());
                addSurvUI.setVisible(true);
            });
            menu.add(addSurveillant);
        }

        menu.add(viewPlanning);

        return menu;
    }

    private JMenu createExamensMenu() {
        JMenu menu = new JMenu("Examens");

        JMenuItem viewExamens = new JMenuItem("Planning des examens");
        viewExamens.addActionListener(e -> showExamensPlanning());

        menu.add(viewExamens);

        if (currentUser.getRole() == Role.ADMIN) {
            JMenuItem addExamen = new JMenuItem("Ajouter un examen");
            addExamen.addActionListener(e -> showAddExamen());
            menu.add(addExamen);
        }

        return menu;
    }

    private JMenu createReportsMenu() {
        JMenu menu = new JMenu("Rapports");

        JMenuItem convocations = new JMenuItem("Générer les convocations");
        convocations.addActionListener(e -> generateConvocations());

        JMenuItem stats = new JMenuItem("Statistiques");
        stats.addActionListener(e -> showStatistics());

        menu.add(convocations);
        menu.add(stats);

        return menu;
    }

    private void createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        // Ajouter les boutons de la barre d'outils
        JButton refreshButton = new JButton("Actualiser");
        refreshButton.addActionListener(e -> refresh());
        toolBar.add(refreshButton);

        add(toolBar, BorderLayout.NORTH);
    }

    private void createStatusBar() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel();
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private void createContentPanel() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(contentPanel, BorderLayout.CENTER);
    }

    private void updateStatusBar() {
        statusLabel.setText("Connecté en tant que : " + currentUser.getUsername() +
                " (" + currentUser.getRole().getDisplayName() + ")");
    }

    // Méthodes d'action
    private void showProfile() {
        // TODO: Implémenter l'affichage du profil
    }

    private void showSettings() {
        // TODO: Implémenter les paramètres
    }

    private void logout() {
        if (JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment vous déconnecter ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            dispose();
            new LoginUI().setVisible(true);
        }
    }

    private void showPlanning() {
        // TODO: Implémenter l'affichage du planning
    }

    private void showExamensPlanning() {
        // TODO: Implémenter l'affichage du planning des examens
    }

    private void showAddExamen() {
        // TODO: Implémenter l'ajout d'examen
    }

    private void generateConvocations() {
        // TODO: Implémenter la génération des convocations
    }

    private void showStatistics() {
        // TODO: Implémenter l'affichage des statistiques
    }

    private void refresh() {
        // TODO: Implémenter le rafraîchissement des données
    }

    // Méthode pour mettre à jour le contenu principal
    public void setContent(JPanel newContent) {
        contentPanel.removeAll();
        contentPanel.add(newContent, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}