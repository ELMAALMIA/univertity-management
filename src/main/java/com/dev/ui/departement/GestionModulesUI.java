package com.dev.ui.departement;

import com.dev.dao.ModuleDAO;
import com.dev.models.Filiere;
import com.dev.models.Module;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class GestionModulesUI extends JFrame {
    private final Filiere filiere;
    private final ModuleDAO moduleDAO;
    private final JTable modulesTable;
    private final DefaultTableModel tableModel;

    public GestionModulesUI(Filiere filiere) {
        this.filiere = filiere;
        this.moduleDAO = new ModuleDAO();

        setTitle("Modules - " + filiere.getNom());
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Création du panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Création du modèle de table
        String[] columns = {"ID", "Nom du module", "Semestre", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Configuration de la table
        modulesTable = new JTable(tableModel);
        modulesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        modulesTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        modulesTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        modulesTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        modulesTable.getColumnModel().getColumn(3).setPreferredWidth(150);

        // Panel des boutons
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(modulesTable), BorderLayout.CENTER);

        add(mainPanel);

        // Chargement initial des données
        loadModules();

        setLocationRelativeTo(null);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addButton = new JButton("Ajouter un module");
        JButton refreshButton = new JButton("Actualiser");

        addButton.addActionListener(e -> showAddModuleDialog());
        refreshButton.addActionListener(e -> loadModules());

        panel.add(addButton);
        panel.add(refreshButton);

        return panel;
    }

    private void loadModules() {
        tableModel.setRowCount(0);
        List<Module> modules = moduleDAO.findByFiliereId(filiere.getId());

        for (Module module : modules) {
            Object[] row = {
                    module.getId(),
                    module.getNom(),
                    module.getSemestre(),
                    "Actions"
            };
            tableModel.addRow(row);
        }
    }

    private void showAddModuleDialog() {
        JDialog dialog = new JDialog(this, "Ajouter un module", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Composants du formulaire
        JTextField nomField = new JTextField(30);
        SpinnerModel spinnerModel = new SpinnerNumberModel(1, 1, 6, 1);
        JSpinner semestreSpinner = new JSpinner(spinnerModel);

        // Layout
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Nom:"), gbc);
        gbc.gridx = 1;
        dialog.add(nomField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Semestre:"), gbc);
        gbc.gridx = 1;
        dialog.add(semestreSpinner, gbc);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");

        saveButton.addActionListener(e -> {
            try {
                String nom = nomField.getText().trim();
                int semestre = (Integer) semestreSpinner.getValue();

                if (!nom.isEmpty()) {
                    Module module = new Module(nom, semestre, filiere.getId());
                    module = moduleDAO.save(module);
                    loadModules();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this,
                            "Module ajouté avec succès",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Veuillez remplir tous les champs",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Erreur lors de l'ajout : " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void editModule(int moduleId) {
        moduleDAO.findById(moduleId).ifPresent(module -> {
            JDialog dialog = new JDialog(this, "Modifier le module", true);
            // ... Configuration similaire à showAddModuleDialog()
        });
    }

    private void deleteModule(int moduleId) {
        if (JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer ce module ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                moduleDAO.delete(moduleId);
                loadModules();
                JOptionPane.showMessageDialog(this,
                        "Module supprimé avec succès",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la suppression : " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}