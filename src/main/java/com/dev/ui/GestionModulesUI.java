package com.dev.ui;

import com.dev.models.Filiere;
import com.dev.models.Module;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

public class GestionModulesUI extends JFrame {
    private Filiere filiere;
    private JTable modulesTable;
    private DefaultTableModel tableModel;

    public GestionModulesUI(Filiere filiere) {
        this.filiere = filiere;

        setTitle("Modules - " + filiere.getNom());
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Création du modèle de table
        String[] columns = {"Nom du module", "Semestre"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rend la table non éditable
            }
        };

        // Création de la table
        modulesTable = new JTable(tableModel);
        modulesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Ajout des modules existants à la table
        for (Module module : filiere.getModules()) {
            Object[] row = {module.getNom(), module.getSemestre()};
            tableModel.addRow(row);
        }

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Ajouter");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // Ajout des composants à la fenêtre
        add(new JScrollPane(modulesTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Actions des boutons
        addButton.addActionListener(e -> showAddModuleDialog());
        editButton.addActionListener(e -> editSelectedModule());
        deleteButton.addActionListener(e -> deleteSelectedModule());

        // Centrer la fenêtre
        setLocationRelativeTo(null);
    }

    private void showAddModuleDialog() {
        JDialog dialog = new JDialog(this, "Ajouter un module", true);
        dialog.setLayout(new GridLayout(3, 2, 5, 5));

        JTextField nomField = new JTextField();
        SpinnerModel spinnerModel = new SpinnerNumberModel(1, 1, 6, 1);
        JSpinner semestreSpinner = new JSpinner(spinnerModel);

        dialog.add(new JLabel("Nom:"));
        dialog.add(nomField);
        dialog.add(new JLabel("Semestre:"));
        dialog.add(semestreSpinner);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            String nom = nomField.getText().trim();
            int semestre = (Integer) semestreSpinner.getValue();

            if (!nom.isEmpty()) {
                Module module = new Module(nom, semestre);
                filiere.ajouterModule(module);
                Object[] row = {module.getNom(), module.getSemestre()};
                tableModel.addRow(row);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Veuillez remplir tous les champs",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(okButton);
        dialog.add(cancelButton);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void editSelectedModule() {
        int selectedRow = modulesTable.getSelectedRow();
        if (selectedRow >= 0) {
            String currentNom = (String) tableModel.getValueAt(selectedRow, 0);
            int currentSemestre = (Integer) tableModel.getValueAt(selectedRow, 1);

            JDialog dialog = new JDialog(this, "Modifier le module", true);
            dialog.setLayout(new GridLayout(3, 2, 5, 5));

            JTextField nomField = new JTextField(currentNom);
            SpinnerModel spinnerModel = new SpinnerNumberModel(currentSemestre, 1, 6, 1);
            JSpinner semestreSpinner = new JSpinner(spinnerModel);

            dialog.add(new JLabel("Nom:"));
            dialog.add(nomField);
            dialog.add(new JLabel("Semestre:"));
            dialog.add(semestreSpinner);

            JButton okButton = new JButton("OK");
            okButton.addActionListener(e -> {
                String nom = nomField.getText().trim();
                int semestre = (Integer) semestreSpinner.getValue();

                if (!nom.isEmpty()) {
                    Module module = filiere.getModules().get(selectedRow);
                    module.setNom(nom);
                    module.setSemestre(semestre);

                    tableModel.setValueAt(nom, selectedRow, 0);
                    tableModel.setValueAt(semestre, selectedRow, 1);

                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Veuillez remplir tous les champs",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            JButton cancelButton = new JButton("Annuler");
            cancelButton.addActionListener(e -> dialog.dispose());

            dialog.add(okButton);
            dialog.add(cancelButton);

            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        }
    }

    private void deleteSelectedModule() {
        int selectedRow = modulesTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirmation = JOptionPane.showConfirmDialog(this,
                    "Êtes-vous sûr de vouloir supprimer ce module ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (confirmation == JOptionPane.YES_OPTION) {
                filiere.getModules().remove(selectedRow);
                tableModel.removeRow(selectedRow);
            }
        }
    }
}