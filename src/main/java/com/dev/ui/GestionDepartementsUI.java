package com.dev.ui;

import com.dev.models.Departement;
import javax.swing.*;
import java.awt.*;

public class GestionDepartementsUI extends JFrame {
    private JList<Departement> departementsList;
    private DefaultListModel<Departement> listModel;

    public GestionDepartementsUI() {
        setTitle("Gestion des Départements");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Panel gauche avec la liste des départements
        listModel = new DefaultListModel<>();
        departementsList = new JList<>(listModel);
        add(new JScrollPane(departementsList), BorderLayout.WEST);

        // Panel droite avec les boutons
        JPanel buttonsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        JButton addButton = new JButton("Ajouter");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");
        JButton filieresButton = new JButton("Gérer les filières");

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(filieresButton);

        add(buttonsPanel, BorderLayout.EAST);

        // Actions des boutons
        addButton.addActionListener(e -> showAddDepartementDialog());
        editButton.addActionListener(e -> editSelectedDepartement());
        deleteButton.addActionListener(e -> deleteSelectedDepartement());
        filieresButton.addActionListener(e -> openFilieresManager());
    }

    private void showAddDepartementDialog() {
        String nom = JOptionPane.showInputDialog(this, "Nom du département :");
        if (nom != null && !nom.trim().isEmpty()) {
            Departement dept = new Departement(nom);
            listModel.addElement(dept);
        }
    }

    private void editSelectedDepartement() {
        Departement selected = departementsList.getSelectedValue();
        if (selected != null) {
            String newNom = JOptionPane.showInputDialog(this,
                    "Nouveau nom :", selected.getNom());
            if (newNom != null && !newNom.trim().isEmpty()) {
                // Mettre à jour le département
            }
        }
    }

    private void deleteSelectedDepartement() {
        int index = departementsList.getSelectedIndex();
        if (index != -1) {
            if (JOptionPane.showConfirmDialog(this,
                    "Voulez-vous vraiment supprimer ce département ?",
                    "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                listModel.remove(index);
            }
        }
    }

    private void openFilieresManager() {
        Departement selected = departementsList.getSelectedValue();
        if (selected != null) {
            new GestionFilieresUI(selected).setVisible(true);
        }
    }
}