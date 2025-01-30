package com.dev.ui.examen;

import com.dev.dao.AffectationDAO;
import com.dev.dao.ExamenDAO;
import com.dev.dao.LocalDAO;
import com.dev.dao.ModuleDAO;
import com.dev.models.Affectation;
import com.dev.models.Examen;
import com.dev.models.Local;
import com.dev.models.Module;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class AffectationListUI extends JFrame {
    private final JTable affectationTable;
    private final JButton addButton;
    private final JButton deleteButton;
    private final JButton updateButton;
    private final JTextField searchField;
    private final JButton searchButton;

    public AffectationListUI(int departementId) {  // Ajout du paramètre departementId
        setTitle("Gestion des Affectations d'Examens");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // Changé de EXIT_ON_CLOSE à DISPOSE_ON_CLOSE
        setLayout(new BorderLayout(10, 10));

        // Panel de recherche
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());

        searchField = new JTextField(20);
        searchButton = new JButton("Rechercher");
        searchButton.addActionListener(this::searchAffectations);
        searchPanel.add(new JLabel("Recherche par Examen ou Local :"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Tableau des affectations
        affectationTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(affectationTable);
        loadAffectations();

        // Panel des boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        addButton = new JButton("Ajouter");
        addButton.addActionListener(this::openAddAffectationUI);
        buttonPanel.add(addButton);

        deleteButton = new JButton("Supprimer");
        deleteButton.addActionListener(this::deleteAffectation);
        buttonPanel.add(deleteButton);

        updateButton = new JButton("Modifier");
        updateButton.addActionListener(this::updateAffectation);
        buttonPanel.add(updateButton);

        // Ajout des composants au cadre
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);


    }

    void loadAffectations() {
        AffectationDAO affectationDAO = new AffectationDAO();
        List<Affectation> affectations = affectationDAO.findAll();

        // Créer un modèle de tableau pour afficher les affectations
        String[] columnNames = {"Module", "Semestre", "Local", "Date", "Heure Début", "Heure Fin"}; // Colonnes mises à jour
        Object[][] data = new Object[affectations.size()][6];

        for (int i = 0; i < affectations.size(); i++) {
            Affectation affectation = affectations.get(i);
            ExamenDAO examenDAO = new ExamenDAO();
            LocalDAO localDAO = new LocalDAO();
            ModuleDAO moduleDAO = new ModuleDAO(); // Ajout de ModuleDAO

            Examen examen = examenDAO.findById(affectation.getExamenId()).orElse(null);
            Local local = localDAO.findById(affectation.getLocalId()).orElse(null);

            // Récupérer le module associé à l'examen
            Module module = null;
            if (examen != null) {
                module = moduleDAO.findById(examen.getModuleId()).orElse(null);
            }

            // Remplir les données du tableau
            data[i][0] = module != null ? module.getNom() : "N/A"; // Nom du module
            data[i][1] = module != null ? module.getSemestre() : "N/A"; // Semestre du module
            data[i][2] = local != null ? local.getNom() : "N/A"; // Nom du local
            data[i][3] = examen != null ? examen.getDateExamen() : "N/A"; // Date de l'examen
            data[i][4] = examen != null ? examen.getHeureDebut() : "N/A"; // Heure de début
            data[i][5] = examen != null ? examen.getHeureFin() : "N/A"; // Heure de fin
        }

        affectationTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }

    private void searchAffectations(ActionEvent e) {
        String searchText = searchField.getText().toLowerCase();
        AffectationDAO affectationDAO = new AffectationDAO();
        List<Affectation> affectations = affectationDAO.findAll();

        // Filtrer les affectations en fonction du texte de recherche
        affectations.removeIf(affectation -> {
            ExamenDAO examenDAO = new ExamenDAO();
            LocalDAO localDAO = new LocalDAO();
            ModuleDAO moduleDAO = new ModuleDAO(); // Ajout de ModuleDAO

            Examen examen = examenDAO.findById(affectation.getExamenId()).orElse(null);
            Local local = localDAO.findById(affectation.getLocalId()).orElse(null);
            Module module = null;
            if (examen != null) {
                module = moduleDAO.findById(examen.getModuleId()).orElse(null);
            }

            // Vérifier si le texte de recherche correspond au nom du module, au nom du local ou à la date de l'examen
            return !(module != null && module.getNom().toLowerCase().contains(searchText)) &&
                    !(local != null && local.getNom().toLowerCase().contains(searchText)) &&
                    !(examen != null && examen.getDateExamen().toString().toLowerCase().contains(searchText));
        });

        // Mettre à jour le tableau avec les résultats de la recherche
        String[] columnNames = {"Module", "Semestre", "Local", "Date", "Heure Début", "Heure Fin"};
        Object[][] data = new Object[affectations.size()][6];

        for (int i = 0; i < affectations.size(); i++) {
            Affectation affectation = affectations.get(i);
            ExamenDAO examenDAO = new ExamenDAO();
            LocalDAO localDAO = new LocalDAO();
            ModuleDAO moduleDAO = new ModuleDAO();

            Examen examen = examenDAO.findById(affectation.getExamenId()).orElse(null);
            Local local = localDAO.findById(affectation.getLocalId()).orElse(null);
            Module module = null;
            if (examen != null) {
                module = moduleDAO.findById(examen.getModuleId()).orElse(null);
            }

            data[i][0] = module != null ? module.getNom() : "N/A";
            data[i][1] = module != null ? module.getSemestre() : "N/A";
            data[i][2] = local != null ? local.getNom() : "N/A";
            data[i][3] = examen != null ? examen.getDateExamen() : "N/A";
            data[i][4] = examen != null ? examen.getHeureDebut() : "N/A";
            data[i][5] = examen != null ? examen.getHeureFin() : "N/A";
        }

        affectationTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }

    private void openAddAffectationUI(ActionEvent e) {
        new AffectationExamenUI(this);
    }

    private void deleteAffectation(ActionEvent e) {
        int selectedRow = affectationTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                String moduleName = (String) affectationTable.getValueAt(selectedRow, 0);
                String localName = (String) affectationTable.getValueAt(selectedRow, 2);

                // Confirmer la suppression
                int response = JOptionPane.showConfirmDialog(
                        this,
                        "Voulez-vous vraiment supprimer cette affectation ?",
                        "Confirmation de suppression",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (response == JOptionPane.YES_OPTION) {
                    // Rechercher l'affectation à supprimer
                    AffectationDAO affectationDAO = new AffectationDAO();
                    ExamenDAO examenDAO = new ExamenDAO();
                    LocalDAO localDAO = new LocalDAO();
                    ModuleDAO moduleDAO = new ModuleDAO();

                    // Trouver l'examen et le local correspondants
                    List<Affectation> affectations = affectationDAO.findAll();
                    for (Affectation aff : affectations) {
                        Examen examen = examenDAO.findById(aff.getExamenId()).orElse(null);
                        Local local = localDAO.findById(aff.getLocalId()).orElse(null);

                        if (examen != null && local != null) {
                            Module module = moduleDAO.findById(examen.getModuleId()).orElse(null);
                            if (module != null &&
                                    module.getNom().equals(moduleName) &&
                                    local.getNom().equals(localName)) {

                                // Utiliser la méthode supprimerAffectation avec examen_id et local_id
                                affectationDAO.supprimerAffectation(aff.getExamenId(), aff.getLocalId());
                                loadAffectations();

                                JOptionPane.showMessageDialog(
                                        this,
                                        "Affectation supprimée avec succès",
                                        "Succès",
                                        JOptionPane.INFORMATION_MESSAGE
                                );
                                return;
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "Erreur lors de la suppression: " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Veuillez sélectionner une affectation à supprimer",
                    "Attention",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }
    private void updateAffectation(ActionEvent e) {
        int selectedRow = affectationTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                String moduleName = (String) affectationTable.getValueAt(selectedRow, 0);
                String localName = (String) affectationTable.getValueAt(selectedRow, 2);

                // Rechercher l'affectation à modifier
                AffectationDAO affectationDAO = new AffectationDAO();
                ExamenDAO examenDAO = new ExamenDAO();
                LocalDAO localDAO = new LocalDAO();
                ModuleDAO moduleDAO = new ModuleDAO();

                List<Affectation> affectations = affectationDAO.findAll();
                for (Affectation aff : affectations) {
                    Examen examen = examenDAO.findById(aff.getExamenId()).orElse(null);
                    Local currentLocal = localDAO.findById(aff.getLocalId()).orElse(null);

                    if (examen != null && currentLocal != null) {
                        Module module = moduleDAO.findById(examen.getModuleId()).orElse(null);
                        if (module != null &&
                                module.getNom().equals(moduleName) &&
                                currentLocal.getNom().equals(localName)) {

                            // Créer la boîte de dialogue de modification
                            JDialog updateDialog = new JDialog(this, "Modifier l'affectation", true);
                            updateDialog.setSize(new Dimension(800, 600));
                            updateDialog.setLayout(new BorderLayout(10, 10));

                            // Panel d'informations
                            JPanel infoPanel = new JPanel(new GridLayout(5, 1, 5, 5));
                            infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                            // Afficher les informations actuelles
                            infoPanel.add(new JLabel("Module: " + module.getNom()));
                            infoPanel.add(new JLabel("Semestre: " + module.getSemestre()));
                            infoPanel.add(new JLabel("Date: " + examen.getDateExamen()));
                            infoPanel.add(new JLabel("Horaire: " + examen.getHeureDebut() + " - " + examen.getHeureFin()));

                            // ComboBox pour les locaux disponibles
                            List<Local> availableLocals = localDAO.findAvailableLocalsForExamen(
                                    examen.getDateExamen(),
                                    examen.getHeureDebut(),
                                    examen.getHeureFin()
                            );

                            // Ajouter le local actuel s'il n'est pas dans la liste
                            if (!availableLocals.contains(currentLocal)) {
                                availableLocals.add(currentLocal);
                            }

                            JComboBox<String> localComboBox = new JComboBox<>();
                            for (Local local : availableLocals) {
                                String localInfo = String.format("%s (Capacité: %d)",
                                        local.getNom(), local.getCapacite());
                                localComboBox.addItem(localInfo);
                                if (local.getId() == currentLocal.getId()) {
                                    localComboBox.setSelectedItem(localInfo);
                                }
                            }

                            // Panel pour la sélection du local
                            JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                            selectionPanel.add(new JLabel("Nouveau local: "));
                            selectionPanel.add(localComboBox);
                            infoPanel.add(selectionPanel);

                            // Boutons
                            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                            JButton saveButton = new JButton("Enregistrer");
                            JButton cancelButton = new JButton("Annuler");

                            // Action du bouton Enregistrer
                            saveButton.addActionListener(evt -> {
                                try {
                                    int selectedIndex = localComboBox.getSelectedIndex();
                                    if (selectedIndex >= 0) {
                                        Local selectedLocal = availableLocals.get(selectedIndex);
                                        aff.setLocalId(selectedLocal.getId());
                                        affectationDAO.update(aff);

                                        loadAffectations(); // Recharger la table
                                        updateDialog.dispose();

                                        JOptionPane.showMessageDialog(
                                                this,
                                                "Affectation mise à jour avec succès",
                                                "Succès",
                                                JOptionPane.INFORMATION_MESSAGE
                                        );
                                    }
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(
                                            updateDialog,
                                            "Erreur lors de la mise à jour: " + ex.getMessage(),
                                            "Erreur",
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                }
                            });

                            cancelButton.addActionListener(evt -> updateDialog.dispose());

                            buttonPanel.add(saveButton);
                            buttonPanel.add(cancelButton);

                            // Assemblage de la boîte de dialogue
                            updateDialog.add(infoPanel, BorderLayout.CENTER);
                            updateDialog.add(buttonPanel, BorderLayout.SOUTH);
                            updateDialog.pack();
                            updateDialog.setLocationRelativeTo(this);
                            updateDialog.setVisible(true);

                            return;
                        }
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Erreur lors de la modification: " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Veuillez sélectionner une affectation à modifier",
                    "Attention",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

}