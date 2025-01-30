package com.dev.ui.departement;

import com.dev.dao.DepartementDAO;
import com.dev.models.Departement;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GestionDepartementsUI extends JFrame {
    private JList<Departement> departementsList;
    private DefaultListModel<Departement> listModel;
    private DepartementDAO departementDAO;

    public GestionDepartementsUI() {
        // Initialisation
        setTitle("Gestion des Départements");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        departementDAO = new DepartementDAO();
        listModel = new DefaultListModel<>();
        departementsList = new JList<>(listModel);

        // Charger les départements depuis la base de données
        loadDepartementsFromDB();

        // Panel gauche avec la liste des départements
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
        addButton.addActionListener(e -> addDepartement());
        editButton.addActionListener(e -> editDepartement());
        deleteButton.addActionListener(e -> deleteDepartement());
        filieresButton.addActionListener(e -> openFilieresManager());

        // Centrer la fenêtre
        setLocationRelativeTo(null);
    }

    private void loadDepartementsFromDB() {
        try {
            List<Departement> departements = departementDAO.findAll();
            listModel.clear();
            for (Departement departement : departements) {
                listModel.addElement(departement);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des départements : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addDepartement() {
        String nom = JOptionPane.showInputDialog(this, "Nom du département :");
        if (nom != null && !nom.trim().isEmpty()) {
            Departement newDepartement = new Departement(nom);
            try {
                departementDAO.save(newDepartement);
                listModel.addElement(newDepartement);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'ajout du département : " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editDepartement() {
        Departement selected = departementsList.getSelectedValue();
        if (selected != null) {
            String newNom = JOptionPane.showInputDialog(this,
                    "Nouveau nom :", selected.getNom());
            if (newNom != null && !newNom.trim().isEmpty()) {
                try {
                    selected.setNom(newNom);
                    departementDAO.update(selected);
                    departementsList.repaint();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "Erreur lors de la mise à jour du département : " + e.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un département à modifier.",
                    "Avertissement", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteDepartement() {
        Departement selected = departementsList.getSelectedValue();
        if (selected != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Voulez-vous vraiment supprimer ce département ?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    departementDAO.delete(selected.getId());
                    listModel.removeElement(selected);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "Erreur lors de la suppression du département : " + e.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un département à supprimer.",
                    "Avertissement", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void openFilieresManager() {
        Departement selected = departementsList.getSelectedValue();
        if (selected != null) {
            new GestionFilieresUI(selected).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un département pour gérer ses filières.",
                    "Avertissement", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GestionDepartementsUI().setVisible(true));
    }
}
