package com.dev.ui.surveillance;

import com.dev.dao.*;
import com.dev.models.*;
import com.dev.enums.TypeSurveillant;
import com.dev.models.Module;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class PlanningsurveillanceUI extends JFrame {
    private final SurveillantDAO surveillantDAO;
    private final ExamenDAO examenDAO;
    private final ModuleDAO moduleDAO;
    private final LocalDAO localDAO;
    private final AffectationDAO affectationDAO;
    private final JComboBox<Surveillant> surveillantComboBox;
    private final JTable planningTable;
    private final int departementId;
    private final TypeSurveillant typeSurveillant;

    public PlanningsurveillanceUI(int departementId, TypeSurveillant typeSurveillant) {
        this.departementId = departementId;
        this.typeSurveillant = typeSurveillant;

        // Initialiser les DAOs
        this.surveillantDAO = new SurveillantDAO();
        this.examenDAO = new ExamenDAO();
        this.moduleDAO = new ModuleDAO();
        this.localDAO = new LocalDAO();
        this.affectationDAO = new AffectationDAO();

        // Configuration de la fenêtre
        setTitle("Planning de Surveillance - " +
                (typeSurveillant == TypeSurveillant.ENSEIGNANT ? "Enseignants" : "Personnels Administratifs"));
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Panel de sélection du surveillant
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Charger les surveillants du département selon le type
        List<Surveillant> surveillants = surveillantDAO.findAll().stream()
                .filter(s -> s.getDepartementId() == departementId &&
                        s.getType() == typeSurveillant)
                .collect(Collectors.toList());

        surveillantComboBox = new JComboBox<>(surveillants.toArray(new Surveillant[0]));
        selectionPanel.add(new JLabel("Sélectionner un " +
                (typeSurveillant == TypeSurveillant.ENSEIGNANT ? "enseignant" : "personnel administratif") + " : "));
        selectionPanel.add(surveillantComboBox);

        // Bouton de recherche
        JButton rechercherButton = new JButton("Rechercher");
        rechercherButton.addActionListener(e -> chargerPlanningSurveillant());
        selectionPanel.add(rechercherButton);

        // Tableau de planning
        String[] colonnes = {"Date", "Module", "Heure Début", "Heure Fin", "Local", "Session"};
        planningTable = new JTable(new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        // Mise en page
        add(selectionPanel, BorderLayout.NORTH);
        add(new JScrollPane(planningTable), BorderLayout.CENTER);

        // Options supplémentaires
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton exporterButton = new JButton("Exporter");
        exporterButton.addActionListener(e -> exporterPlanning());

        // Bouton pour générer un planning global
        JButton planningGlobalButton = new JButton("Planning Global");
        planningGlobalButton.addActionListener(e -> genererPlanningGlobal());

        buttonPanel.add(exporterButton);
        buttonPanel.add(planningGlobalButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void chargerPlanningSurveillant() {
        // Vérifier qu'un surveillant est sélectionné
        Surveillant surveillantSelectionne = (Surveillant) surveillantComboBox.getSelectedItem();
        if (surveillantSelectionne == null) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un surveillant",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Formateurs pour la date et l'heure
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter heureFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Modèle de tableau
        DefaultTableModel model = (DefaultTableModel) planningTable.getModel();
        model.setRowCount(0);

        // Récupérer tous les surveillants du département et du type
        List<Surveillant> surveillants = surveillantDAO.findAll().stream()
                .filter(s -> s.getDepartementId() == departementId &&
                        s.getType() == typeSurveillant)
                .collect(Collectors.toList());

        // Récupérer les examens du surveillant sélectionné
        List<Examen> examens = affectationDAO.findBySurveillantId(surveillantSelectionne.getId());
        System.out.println(surveillantSelectionne.getId());
        System.out.println(examens);
        // Remplir le planning
        for (Examen examen : examens) {
            // Récupérer les informations du module
            Module module = moduleDAO.findById(examen.getModuleId()).orElse(null);
            String moduleName = module != null ? module.getNom() : "N/A";

            // Récupérer le local
            Local local = localDAO.findByExamenAndSurveillant(examen.getId(), surveillantSelectionne.getId()).orElse(null);
            String localName = local != null ? local.getNom() : "N/A";

            model.addRow(new Object[]{
                    examen.getDateExamen().format(dateFormatter),
                    moduleName,
                    examen.getHeureDebut().format(heureFormatter),
                    examen.getHeureFin().format(heureFormatter),
                    localName,
                    examen.getSessionType().getDisplayName()
            });
        }

        // Message si aucune affectation
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Aucune affectation de surveillance trouvée pour ce surveillant",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    private void genererPlanningGlobal() {
        // Créer un dialogue pour afficher le planning global de tous les surveillants du type sélectionné
        JDialog dialogPlanningGlobal = new JDialog(this,
                "Planning Global " +
                        (typeSurveillant == TypeSurveillant.ENSEIGNANT ? "des Enseignants" : "du Personnel Administratif"),
                true);
        dialogPlanningGlobal.setSize(1200, 800);

        // Tableau pour le planning global
        String[] colonnes = {"Surveillant", "Date", "Module", "Heure Début", "Heure Fin", "Local", "Session"};
        DefaultTableModel modelGlobal = new DefaultTableModel(colonnes, 0);
        JTable tableauPlanningGlobal = new JTable(modelGlobal);

        // Récupérer tous les surveillants du département et du type
        List<Surveillant> surveillants = surveillantDAO.findAll().stream()
                .filter(s -> s.getDepartementId() == departementId &&
                        s.getType() == typeSurveillant)
                .collect(Collectors.toList());

        // Remplir le planning global
        for (Surveillant surveillant : surveillants) {
            List<Examen> examens = affectationDAO.findBySurveillantId(surveillant.getId());

            for (Examen examen : examens) {
                // Récupérer les informations du module
                Module module = moduleDAO.findById(examen.getModuleId()).orElse(null);
                String moduleName = module != null ? module.getNom() : "N/A";

                // Récupérer le local
                Local local = localDAO.findByExamenAndSurveillant(examen.getId(), surveillant.getId()).orElse(null);
                String localName = local != null ? local.getNom() : "N/A";

                modelGlobal.addRow(new Object[]{
                        surveillant.getNom() + " " + surveillant.getPrenom(),
                        examen.getDateExamen(),
                        moduleName,
                        examen.getHeureDebut(),
                        examen.getHeureFin(),
                        localName,
                        examen.getSessionType().getDisplayName()
                });
            }
        }

        // Ajouter le tableau dans un scroll pane
        dialogPlanningGlobal.add(new JScrollPane(tableauPlanningGlobal));

        // Bouton d'exportation du planning global
        JButton exporterGlobalButton = new JButton("Exporter Planning Global");
        exporterGlobalButton.addActionListener(e -> exporterPlanningGlobal(modelGlobal));
        dialogPlanningGlobal.add(exporterGlobalButton, BorderLayout.SOUTH);

        dialogPlanningGlobal.setLocationRelativeTo(this);
        dialogPlanningGlobal.setVisible(true);
    }

    private void exporterPlanningGlobal(DefaultTableModel modelGlobal) {
        // Ouvrir un dialogue de choix de fichier
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter le planning global");
        fileChooser.setSelectedFile(new java.io.File(
                "PlanningGlobal_" +
                        (typeSurveillant == TypeSurveillant.ENSEIGNANT ? "Enseignants" : "PersonnelAdministratif") +
                        ".csv"
        ));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            try (java.io.PrintWriter out = new java.io.PrintWriter(fileToSave)) {
                // Écrire l'en-tête
                out.println("Surveillant,Date,Module,Heure Début,Heure Fin,Local,Session");

                // Écrire les données
                for (int i = 0; i < modelGlobal.getRowCount(); i++) {
                    out.println(
                            modelGlobal.getValueAt(i, 0) + "," +
                                    modelGlobal.getValueAt(i, 1) + "," +
                                    modelGlobal.getValueAt(i, 2) + "," +
                                    modelGlobal.getValueAt(i, 3) + "," +
                                    modelGlobal.getValueAt(i, 4) + "," +
                                    modelGlobal.getValueAt(i, 5) + "," +
                                    modelGlobal.getValueAt(i, 6)
                    );
                }

                JOptionPane.showMessageDialog(this,
                        "Planning global exporté avec succès",
                        "Exportation",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'exportation : " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exporterPlanning() {
        // Vérifier qu'un surveillant est sélectionné
        Surveillant surveillantSelectionne = (Surveillant) surveillantComboBox.getSelectedItem();
        if (surveillantSelectionne == null) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un surveillant à exporter",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Ouvrir un dialogue de choix de fichier
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter le planning");
        fileChooser.setSelectedFile(new java.io.File(
                "Planning_" + surveillantSelectionne.getNom() + "_" +
                        surveillantSelectionne.getPrenom() + ".csv"
        ));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            try (java.io.PrintWriter out = new java.io.PrintWriter(fileToSave)) {
                // Écrire l'en-tête
                out.println("Date,Module,Heure Début,Heure Fin,Local,Session");

                // Écrire les données
                for (int i = 0; i < planningTable.getRowCount(); i++) {
                    out.println(
                            planningTable.getValueAt(i, 0) + "," +
                                    planningTable.getValueAt(i, 1) + "," +
                                    planningTable.getValueAt(i, 2) + "," +
                                    planningTable.getValueAt(i, 3) + "," +
                                    planningTable.getValueAt(i, 4) + "," +
                                    planningTable.getValueAt(i, 5)
                    );
                }

                JOptionPane.showMessageDialog(this,
                        "Planning exporté avec succès",
                        "Exportation",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'exportation : " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Méthode statique pour afficher le dialogue
    public static void show(JFrame parent, int departementId, TypeSurveillant typeSurveillant) {
        PlanningsurveillanceUI dialog = new PlanningsurveillanceUI(departementId, typeSurveillant);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}