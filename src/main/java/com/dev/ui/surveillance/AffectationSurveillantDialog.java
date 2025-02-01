package com.dev.ui.surveillance;

import com.dev.dao.ExamenDAO;
import com.dev.dao.SurveillantDAO;
import com.dev.dao.AffectationSurveillantDAO;
import com.dev.dao.ModuleDAO;
import com.dev.enums.Role;
import com.dev.models.Examen;
import com.dev.models.Surveillant;
import com.dev.models.Module;
import com.dev.enums.TypeSurveillant;
import com.dev.models.AffectationSurveillant;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class AffectationSurveillantDialog extends JDialog {
    private final JTable examTable;
    private final JTable surveillantTable;
    private final ExamenDAO examenDAO;
    private final SurveillantDAO surveillantDAO;
    private final AffectationSurveillantDAO affectationDAO;
    private final ModuleDAO moduleDAO;
    private final int departementId;
    private final TypeSurveillant userType;
    private final Role role;

    public AffectationSurveillantDialog(JFrame parent, int departementId, TypeSurveillant userType, Role role) {
        super(parent, "Affectation des Surveillants", true);

        this.departementId = departementId;
        this.userType = userType;
        this.role = role;

        // Initialisation des DAOs
        this.examenDAO = new ExamenDAO();
        this.surveillantDAO = new SurveillantDAO();
        this.affectationDAO = new AffectationSurveillantDAO();
        this.moduleDAO = new ModuleDAO();

        // Configuration de la fenêtre
        setSize(800, 600);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(parent);

        // Créer les modèles de tableau
        DefaultTableModel examModel = new DefaultTableModel(
                new String[]{"ID", "Module", "Date", "Heure", "Session"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        DefaultTableModel surveillantModel = new DefaultTableModel(
                new String[]{"ID", "Nom", "Prénom", "Type"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Initialiser les tables
        examTable = new JTable(examModel);
        examTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        surveillantTable = new JTable(surveillantModel);
        surveillantTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Création des panneaux
        JPanel examPanel = createExamPanel();
        JPanel surveillantPanel = createSurveillantPanel();

        // Bouton d'affectation
        JButton affecterButton = new JButton("Affecter");
        affecterButton.setPreferredSize(new Dimension(100, 30));
        affecterButton.addActionListener(this::affecterSurveillant);

        // Ajout des composants
        add(examPanel, BorderLayout.WEST);
        add(surveillantPanel, BorderLayout.CENTER);
        add(affecterButton, BorderLayout.SOUTH);

        // Charger les données
        chargerExamens();
        chargerSurveillants();
    }

    private JPanel createExamPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Examens"));
        JScrollPane scrollPane = new JScrollPane(examTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSurveillantPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Surveillants"));
        JScrollPane scrollPane = new JScrollPane(surveillantTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void chargerExamens() {
        DefaultTableModel model = (DefaultTableModel) examTable.getModel();
        model.setRowCount(0);

        // Formatter pour la date et l'heure
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter heureFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Récupérer les examens
        List<Examen> examens = examenDAO.findByDepartementId(role, departementId);
        for (Examen examen : examens) {
            // Récupérer le nom du module
            Module module = moduleDAO.findById(examen.getModuleId()).orElse(null);
            String moduleName = module != null ? module.getNom() : "N/A";

            String sessionTypeDisplay = "N/A";
            if (examen.getSessionType() != null) {
                sessionTypeDisplay = examen.getSessionType().getDisplayName();
            }

            model.addRow(new Object[]{
                    examen.getId(),
                    moduleName,
                    examen.getDateExamen().format(dateFormatter),
                    examen.getHeureDebut().format(heureFormatter) + " - " +
                            examen.getHeureFin().format(heureFormatter),
                    sessionTypeDisplay
            });
        }
    }

    private void chargerSurveillants() {
        DefaultTableModel model = (DefaultTableModel) surveillantTable.getModel();
        model.setRowCount(0);

        // Filtrer les surveillants selon le type d'utilisateur
        List<Surveillant> surveillants;
        if (userType == TypeSurveillant.ADMINISTRATIF) {
            surveillants = surveillantDAO.findAll();
        } else {
            // Pour les utilisateurs de département, filtrer par département et type
            surveillants = surveillantDAO.findAll().stream()
                    .filter(s -> s.getDepartementId() == departementId &&
                            s.getType() == TypeSurveillant.ENSEIGNANT)
                    .collect(Collectors.toList());
        }

        for (Surveillant surveillant : surveillants) {
            model.addRow(new Object[]{
                    surveillant.getId(),
                    surveillant.getNom(),
                    surveillant.getPrenom(),
                    surveillant.getType()
            });
        }
    }

    private void affecterSurveillant(ActionEvent e) {
        // Vérifier qu'un examen et un surveillant sont sélectionnés
        int selectedExamRow = examTable.getSelectedRow();
        int selectedSurveillantRow = surveillantTable.getSelectedRow();

        if (selectedExamRow == -1 || selectedSurveillantRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un examen et un surveillant",
                    "Sélection incomplète",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Récupérer l'ID de l'examen et du surveillant
        int examenId = (int) examTable.getValueAt(selectedExamRow, 0);
        int surveillantId = (int) surveillantTable.getValueAt(selectedSurveillantRow, 0);
        String moduleName = (String) examTable.getValueAt(selectedExamRow, 1);

        System.out.println("examen  id "+examenId);
        System.out.println("surveillant id "+surveillantId);
        try {
            // Créer et sauvegarder l'affectation
            AffectationSurveillant affectation = new AffectationSurveillant(
                    examenId,
                    surveillantId
            );
            affectationDAO.save(affectation);

            // Message de succès
            String surveillantNom = (String) surveillantTable.getValueAt(selectedSurveillantRow, 1);
            String surveillantPrenom = (String) surveillantTable.getValueAt(selectedSurveillantRow, 2);

            JOptionPane.showMessageDialog(this,
                    "Affectation réussie :\n" +
                            surveillantPrenom + " " + surveillantNom +
                            " a été affecté(e) à l'examen de " + moduleName,
                    "Affectation réussie",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'affectation : " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Méthode statique pour afficher le dialogue
    public static void show(JFrame parent, int departementId, TypeSurveillant userType, Role role) {
        AffectationSurveillantDialog dialog = new AffectationSurveillantDialog(parent, departementId, userType, role);
        dialog.setVisible(true);
    }
}