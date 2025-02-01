package com.dev.ui.surveillance;

import com.dev.dao.SurveillantDAO;
import com.dev.dao.DepartementDAO;
import com.dev.enums.Role;
import com.dev.enums.TypeSurveillant;
import com.dev.models.Surveillant;
import com.dev.models.Departement;
import com.dev.ui.surveillance.PlanningsurveillanceUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.stream.Collectors;

public class GestionSurveillanceUI extends JFrame {
    private final SurveillantDAO surveillantDAO;
    private final DepartementDAO departementDAO;
    private final JTable surveillantTable;
    private final int departementId;
    private final TypeSurveillant userType;
    private final Role role;

    public GestionSurveillanceUI(int departementId, TypeSurveillant userType, Role role) {
        this.departementId = departementId;
        this.userType = userType;
        this.surveillantDAO = new SurveillantDAO();
        this.departementDAO = new DepartementDAO();

        this.role = role;

        setTitle("Gestion des Surveillances");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Panel du haut avec titre et recherche
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Titre avec info département
        Departement dept = departementDAO.findById(departementId).orElse(null);
        String deptName = dept != null ? dept.getNom() : "Département inconnu";
        JLabel titleLabel = new JLabel("Gestion des Surveillants - " + deptName);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Recherche
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Rechercher");
        searchPanel.add(new JLabel("Rechercher : "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Table des surveillants
        surveillantTable = new JTable();
        surveillantTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(surveillantTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton addButton = new JButton("Ajouter");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");
        JButton affectButton = new JButton("Affecter aux Examens");
        JButton planningButton = new JButton("Voir Planning");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(affectButton);
        buttonPanel.add(planningButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners
        addButton.addActionListener(e -> showAddSurveillantDialog());

        editButton.addActionListener(this::editSurveillant);
        deleteButton.addActionListener(e->deleteSurveillant());
        affectButton.addActionListener(this::showAffectationDialog);
        planningButton.addActionListener(this::showPlanning);
        searchButton.addActionListener(e -> searchSurveillants(searchField.getText()));

        // Initialisation de la table
        initializeTable();
        loadSurveillants();

        // Finalisation
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeTable() {
        String[] columnNames = {
                "ID", "Nom", "Prénom", "Type", "Département", "Statut"
        };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        surveillantTable.setModel(model);
    }

    private void loadSurveillants() {
        DefaultTableModel model = (DefaultTableModel) surveillantTable.getModel();
        model.setRowCount(0);

        List<Surveillant> surveillants;
        if (userType == TypeSurveillant.ADMINISTRATIF) {
            surveillants = surveillantDAO.findAll().stream()
                    .filter(s -> s.getType() == TypeSurveillant.ADMINISTRATIF)
                    .collect(Collectors.toList());
        } else {
            surveillants = surveillantDAO.findAll().stream()
                    .filter(s -> s.getDepartementId() == departementId &&
                            s.getType() == TypeSurveillant.ENSEIGNANT)
                    .collect(Collectors.toList());
        }

        for (Surveillant surveillant : surveillants) {
            Departement dept = departementDAO.findById(surveillant.getDepartementId()).orElse(null);
            model.addRow(new Object[]{
                    surveillant.getId(),
                    surveillant.getNom(),
                    surveillant.getPrenom(),
                    surveillant.getType().getDescription(),
                    dept != null ? dept.getNom() : "N/A",
                    "Disponible" // À modifier selon la logique de disponibilité
            });
        }
    }

    private void showAddSurveillantDialog() {
        JDialog dialog = new JDialog(this, "Ajouter un Surveillant", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Champs
        JTextField nomField = new JTextField(20);
        JTextField prenomField = new JTextField(20);
        JComboBox<TypeSurveillant> typeCombo = new JComboBox<>(TypeSurveillant.values());

        if (userType == TypeSurveillant.ADMINISTRATIF) {
            typeCombo.setSelectedItem(TypeSurveillant.ADMINISTRATIF);
            typeCombo.setEnabled(false);
        } else {
            typeCombo.setSelectedItem(TypeSurveillant.ENSEIGNANT);
            typeCombo.setEnabled(false);
        }

        // Layout
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Nom :"), gbc);
        gbc.gridx = 1;
        dialog.add(nomField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Prénom :"), gbc);
        gbc.gridx = 1;
        dialog.add(prenomField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Type :"), gbc);
        gbc.gridx = 1;
        dialog.add(typeCombo, gbc);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");

        saveButton.addActionListener(e -> {
            if (validateFields(nomField.getText(), prenomField.getText())) {
                Surveillant surveillant = new Surveillant(

                        nomField.getText().trim(),
                        prenomField.getText().trim(),
                        (TypeSurveillant) typeCombo.getSelectedItem(),
                        departementId
                );
                surveillantDAO.save(surveillant);
                loadSurveillants();
                dialog.dispose();
                JOptionPane.showMessageDialog(this,
                        "Surveillant ajouté avec succès",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void editSurveillant(ActionEvent e) {
        int selectedRow = surveillantTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un surveillant à modifier",
                    "Attention",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int surveillantId = (int) surveillantTable.getValueAt(selectedRow, 0);
        Surveillant surveillant = surveillantDAO.findById(surveillantId).orElse(null);

        if (surveillant == null) {
            JOptionPane.showMessageDialog(this,
                    "Surveillant introuvable",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Créer et afficher le dialogue de modification
        showEditSurveillantDialog(surveillant);
    }

    private void showEditSurveillantDialog(Surveillant surveillant) {
        JDialog dialog = new JDialog(this, "Modifier un Surveillant", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Champs pré-remplis avec les données du surveillant
        JTextField nomField = new JTextField(surveillant.getNom(), 20);
        JTextField prenomField = new JTextField(surveillant.getPrenom(), 20);
        JComboBox<TypeSurveillant> typeCombo = new JComboBox<>(TypeSurveillant.values());
        typeCombo.setSelectedItem(surveillant.getType());

        // Désactiver le changement de type selon le rôle utilisateur
        if (userType == TypeSurveillant.ADMINISTRATIF) {
            typeCombo.setSelectedItem(TypeSurveillant.ADMINISTRATIF);
            typeCombo.setEnabled(false);
        } else {
            typeCombo.setSelectedItem(TypeSurveillant.ENSEIGNANT);
            typeCombo.setEnabled(false);
        }

        // Layout
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Nom :"), gbc);
        gbc.gridx = 1;
        dialog.add(nomField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Prénom :"), gbc);
        gbc.gridx = 1;
        dialog.add(prenomField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Type :"), gbc);
        gbc.gridx = 1;
        dialog.add(typeCombo, gbc);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");

        // Action du bouton Enregistrer
        saveButton.addActionListener(e -> {
            if (validateFields(nomField.getText(), prenomField.getText())) {
                // Mise à jour des données du surveillant
                surveillant.setNom(nomField.getText().trim());
                surveillant.setPrenom(prenomField.getText().trim());
                surveillant.setType((TypeSurveillant) typeCombo.getSelectedItem());

                try {
                    // Mise à jour dans la base de données
                    surveillantDAO.update(surveillant);

                    // Rafraîchir la table
                    loadSurveillants();

                    JOptionPane.showMessageDialog(this,
                            "Surveillant modifié avec succès",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);

                    dialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Erreur lors de la modification : " + ex.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Action du bouton Annuler
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        // Affichage du dialogue
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteSurveillant() {
        int selectedRow = surveillantTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un surveillant à supprimer",
                    "Attention",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int surveillantId = (int) surveillantTable.getValueAt(selectedRow, 0);
        String nom = (String) surveillantTable.getValueAt(selectedRow, 1);
        String prenom = (String) surveillantTable.getValueAt(selectedRow, 2);

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer le surveillant " + nom + " " + prenom + " ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                surveillantDAO.delete(surveillantId);
                loadSurveillants();
                JOptionPane.showMessageDialog(this,
                        "Surveillant supprimé avec succès",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void showAffectationDialog(ActionEvent e) {
        // Utiliser la méthode statique de la nouvelle classe de dialogue
        AffectationSurveillantDialog.show(this, departementId, userType,role );
    }
    private void showPlanning(ActionEvent e) {

            PlanningsurveillanceUI.show(this, departementId,userType);

    }

    private void searchSurveillants(String searchText) {
        DefaultTableModel model = (DefaultTableModel) surveillantTable.getModel();
        model.setRowCount(0);


        List<Surveillant> surveillants;
        if (userType == TypeSurveillant.ADMINISTRATIF) {
            surveillants = surveillantDAO.findAll().stream()
                    .filter(s -> s.getType() == TypeSurveillant.ADMINISTRATIF)
                    .collect(Collectors.toList());
        } else {
            surveillants = surveillantDAO.findAll().stream()
                    .filter(s -> s.getDepartementId() == departementId &&
                            s.getType() == TypeSurveillant.ENSEIGNANT)
                    .collect(Collectors.toList());
        }
        List<Surveillant> surveillantsf = surveillants.stream()
                .filter(s -> matchesSearch(s, searchText))
                .collect(Collectors.toList());

        for (Surveillant surveillant : surveillantsf) {
            Departement dept = departementDAO.findById(surveillant.getDepartementId()).orElse(null);
            model.addRow(new Object[]{
                    surveillant.getId(),
                    surveillant.getNom(),
                    surveillant.getPrenom(),
                    surveillant.getType().getDescription(),
                    dept != null ? dept.getNom() : "N/A",
                    "Disponible" // Ce statut pourrait être dynamique selon les affectations
            });
        }
    }

    private boolean matchesSearch(Surveillant surveillant, String searchText) {
        String search = searchText.toLowerCase();
        return surveillant.getNom().toLowerCase().contains(search) ||
                surveillant.getPrenom().toLowerCase().contains(search);
    }

    private boolean validateFields(String nom, String prenom) {
        if (nom.trim().isEmpty() || prenom.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Tous les champs sont obligatoires",
                    "Erreur de validation",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}