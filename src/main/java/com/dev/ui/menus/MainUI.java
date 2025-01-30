package com.dev.ui.menus;

import com.dev.dao.ExamenDAO;
import com.dev.dao.LocalDAO;
import com.dev.dao.ModuleDAO;
import com.dev.enums.Role;
import com.dev.enums.TypeSurveillant;
import com.dev.models.Examen;
import com.dev.models.Local;
import com.dev.models.Surveillant;
import com.dev.models.Utilisateur;
import com.dev.ui.LoginUI;
import com.dev.ui.departement.GestionDepartementsUI;
import com.dev.ui.departement.GestionFilieresUI;
import com.dev.ui.examen.AffectationListUI;

import com.dev.ui.examen.GestionExamenUI;
import com.dev.ui.examen.GestionLocalUI;
import com.dev.ui.surveillance.AffectationSurveillantDialog;
import com.dev.ui.surveillance.GestionSurveillanceUI;
import com.dev.ui.surveillance.PlanningsurveillanceUI;
import com.lowagie.text.DocumentException;


import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
// iText PDF

import java.io.FileOutputStream;
import java.util.List;
import java.util.Optional;

public class MainUI extends JFrame {
    private Utilisateur currentUser;
    private JPanel contentPanel;
    private JLabel statusLabel;

    public MainUI(Utilisateur user) {
        this.currentUser = user;

        System.out.println(user);
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
        showStatistics();
        // Centrer la fenêtre
        setLocationRelativeTo(null);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu Fichier
        JMenu fileMenu = createFileMenu();
        menuBar.add(fileMenu);

        // Menu Département
        if (currentUser.getRole() == Role.ADMIN ) {
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
        JMenu menu = new JMenu("Compte");

        JMenuItem profilItem = new JMenuItem("Gérer mon profil");
        profilItem.addActionListener(e -> showProfile());

        JMenuItem deconnexionItem = new JMenuItem("Se déconnecter");
        deconnexionItem.addActionListener(e -> logout());

        JMenuItem quitterApplicationItem = new JMenuItem("Quitter l'application");
        quitterApplicationItem.addActionListener(e -> System.exit(0));

        menu.add(profilItem);
        menu.add(deconnexionItem);
        menu.addSeparator();
        menu.add(quitterApplicationItem);

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
        JMenu menu = new JMenu("Gestion des surveillants");
        int departementId = currentUser.getDepartementId();

        // Sous-menu Planning - accessible à tous
        JMenu planningMenu = new JMenu("Planning");

        // Les enseignants ne voient que leur propre planning
        if (currentUser.getRole() == Role.CHEF_DEPT) {
            JMenuItem monPlanningItem = new JMenuItem("Mon Planning");
            monPlanningItem.addActionListener(e ->
                    PlanningsurveillanceUI.show(this, departementId, TypeSurveillant.ENSEIGNANT)
            );
            planningMenu.add(monPlanningItem);
        }
        // Les administratifs et chefs de département ont plus d'options
        else {
            JMenuItem planningEnseignantsItem = new JMenuItem("Planning des Enseignants");
            planningEnseignantsItem.addActionListener(e ->
                    PlanningsurveillanceUI.show(this, departementId, TypeSurveillant.ENSEIGNANT)
            );
            planningMenu.add(planningEnseignantsItem);

            // Planning administratif uniquement pour admin et chef de département
            if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.CHEF_DEPT) {
                JMenuItem planningAdministratifsItem = new JMenuItem("Planning Administratif");
                planningAdministratifsItem.addActionListener(e ->
                        PlanningsurveillanceUI.show(this, departementId, TypeSurveillant.ADMINISTRATIF)
                );
                planningMenu.add(planningAdministratifsItem);
            }
        }
        menu.add(planningMenu);

        // Sous-menu Gestion - uniquement pour admin et chef de département
        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.CHEF_DEPT) {
            JMenu gestionMenu = new JMenu("Gestion des Surveillants");

            JMenuItem gestionEnseignantsItem = new JMenuItem("Gestion des Enseignants");
            gestionEnseignantsItem.addActionListener(e -> {
                GestionSurveillanceUI gestionSurveillanceUI = new GestionSurveillanceUI(
                        departementId,
                        TypeSurveillant.ENSEIGNANT
                );
                gestionSurveillanceUI.setVisible(true);
            });
            gestionMenu.add(gestionEnseignantsItem);

            // Gestion des administratifs uniquement pour l'admin
            if (currentUser.getRole() == Role.ADMIN) {
                JMenuItem gestionAdministratifsItem = new JMenuItem("Gestion du Personnel Administratif");
                gestionAdministratifsItem.addActionListener(e -> {
                    GestionSurveillanceUI gestionSurveillanceUI = new GestionSurveillanceUI(
                            departementId,
                            TypeSurveillant.ADMINISTRATIF
                    );
                    gestionSurveillanceUI.setVisible(true);
                });
                gestionMenu.add(gestionAdministratifsItem);
            }

            menu.add(gestionMenu);
        }

        // Sous-menu Affectation - contrôle d'accès strict
        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.CHEF_DEPT) {
            JMenu affectationMenu = new JMenu("Affectation");

            JMenuItem affectationEnseignantsItem = new JMenuItem("Affectation Enseignants");
            affectationEnseignantsItem.addActionListener(e ->
                    AffectationSurveillantDialog.show(this, departementId, TypeSurveillant.ENSEIGNANT)
            );
            affectationMenu.add(affectationEnseignantsItem);

            // Affectation des administratifs uniquement pour l'admin
            if (currentUser.getRole() == Role.ADMIN) {
                JMenuItem affectationAdministratifsItem = new JMenuItem("Affectation Personnel Administratif");
                affectationAdministratifsItem.addActionListener(e ->
                        AffectationSurveillantDialog.show(this, departementId, TypeSurveillant.ADMINISTRATIF)
                );
                affectationMenu.add(affectationAdministratifsItem);
            }

            menu.add(affectationMenu);
        }

        return menu;
    }
    private JMenu createExamensMenu() {
        JMenu menu = new JMenu("Examens");

        // Option pour la gestion des examens
        int departementId = currentUser.getDepartementId();
        JMenuItem gestionExamen = new JMenuItem("Gestion des Examens");
        gestionExamen.addActionListener(e -> {

            System.out.println("departementId: " + departementId);
            System.out.println("currentUser.getRole(): " + currentUser);
            GestionExamenUI gestionExamenUi = new GestionExamenUI(departementId);
            gestionExamenUi.setVisible(true); // Afficher la fenêtre de gestion des examens
        });
        menu.add(gestionExamen);

        // Option pour la gestion des locaux
        JMenuItem gestionLocal = new JMenuItem("Gestion des Locaux");
        gestionLocal.addActionListener(e -> {
            GestionLocalUI gestionLocalUi = new GestionLocalUI();
            gestionLocalUi.setVisible(true); // Afficher la fenêtre de gestion des locaux
        });
        menu.add(gestionLocal);

        // Option pour l'affectation des examens aux locaux
        JMenuItem affectationExamen = new JMenuItem("Affectation Examen aux Locaux");
        affectationExamen.addActionListener(e -> {
            AffectationListUI affectationListUI = new AffectationListUI(departementId); // Créer une nouvelle instance de la liste des affectations

        });
        menu.add(affectationExamen);

        return menu; // Retourner le menu configuré
    }
    private JMenu createReportsMenu() {
        JMenu menu = new JMenu("Rapports");


//        JMenuItem convocations = new JMenuItem("Générer les convocations");
//        convocations.addActionListener(e -> {
//            // Utiliser le departementId de l'utilisateur courant
//           ConvocationsUI.show(currentUser.getDepartementId());
//        });
        JMenuItem stats = new JMenuItem("Statistiques");
        stats.addActionListener(e -> showStatistics());

//        menu.add(convocations);
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
    private void showProfile() {
        ProfilUI profilDialog = new ProfilUI(this, currentUser);
        profilDialog.setVisible(true);
    }

    private void updateStatusBar() {
        statusLabel.setText("Connecté en tant que : " + currentUser.getUsername() +
                " (" + currentUser.getRole().getDisplayName() + ")");
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
    private void showAffectationSurveillant(int departementId, TypeSurveillant userType) {

            AffectationSurveillantDialog.show(this, departementId, userType);

    }

    private void showAddExamen() {
        // TODO: Implémenter l'ajout d'examen
    }

    private void showStatistics() {
        // Créer le panneau de statistiques
        StatistiquesUI statistiquesPanel = new StatistiquesUI(currentUser.getDepartementId());

        // Mettre à jour le contenu principal
        setContent(statistiquesPanel);
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