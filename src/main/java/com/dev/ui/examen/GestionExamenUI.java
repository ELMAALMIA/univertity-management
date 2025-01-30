package com.dev.ui.examen;

import com.dev.dao.DatabaseConnection;
import com.dev.dao.ExamenDAO;
import com.dev.dao.LocalDAO;
import com.dev.dao.ModuleDAO;
import com.dev.enums.SessionType;
import com.dev.models.Examen;
import com.dev.models.Local;
import com.dev.models.Module;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.awt.Desktop;
import java.io.IOException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Color;
import java.awt.Desktop;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
public class GestionExamenUI extends JFrame {
    private final ExamenDAO examenDAO;
    private final ModuleDAO moduleDAO;
    private final JList<Examen> examenList;
    private final DefaultListModel<Examen> listModel;
    private final int departementId;

    public GestionExamenUI(int departementId) {
        this.departementId = departementId;
        this.examenDAO = new ExamenDAO();
        this.moduleDAO = new ModuleDAO();

        // Configuration de la fenêtre
        setTitle("Gestion des Examens - Département");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Création du modèle de liste
        listModel = new DefaultListModel<>();
        examenList = new JList<>(listModel);

        // Dans le constructeur de GestionExamenUI, après la création de la JList
        examenList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = examenList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    Examen examen = listModel.getElementAt(index);
                    Rectangle bounds = examenList.getCellBounds(index, index);

                    // Vérifier si le clic est dans la zone des boutons
                    if (e.getX() > bounds.x + bounds.width - 150) {  // Zone des boutons
                        if (e.getX() > bounds.x + bounds.width - 75) {  // Bouton Supprimer
                            deleteExamen(examen.getId());
                        } else {  // Bouton Modifier
                            showEditExamenDialog(examen);
                        }
                    }
                }
            }
        });

        examenList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Personnalisation de l'affichage des examens
        examenList.setCellRenderer(new ExamenListCellRenderer());

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel des boutons
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(examenList), BorderLayout.CENTER);

        // Panel contextuel (popup menu)
        JPopupMenu popupMenu = createPopupMenu();
        examenList.setComponentPopupMenu(popupMenu);

        add(mainPanel);

        // Panel de recherche
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);

        // Chargement initial des données
        loadExamens();

        setLocationRelativeTo(null);
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Modifier");
        JMenuItem deleteItem = new JMenuItem("Supprimer");

        editItem.addActionListener(e -> {
            Examen selected = examenList.getSelectedValue();
            if (selected != null) {
                showEditExamenDialog(selected);
            }
        });

        deleteItem.addActionListener(e -> {
            Examen selected = examenList.getSelectedValue();
            if (selected != null) {
                deleteExamen(selected.getId());
            }
        });

        popup.add(editItem);
        popup.add(deleteItem);
        return popup;
    }

    private void loadExamens() {
        listModel.clear();
        List<Examen> examens = examenDAO.findByDepartementId(departementId);
        examens.forEach(listModel::addElement);
    }

    // Renderer personnalisé pour l'affichage des examens
// Modifier la classe ExamenListCellRenderer pour inclure les boutons et une meilleure mise en page
    class ExamenListCellRenderer extends JPanel implements ListCellRenderer<Examen> {
        private JPanel infoPanel = new JPanel(new GridLayout(2, 2, 5, 2));
        private JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        private JSeparator separator = new JSeparator();

        private JLabel moduleLabel = new JLabel();
        private JLabel dateLabel = new JLabel();
        private JLabel heureLabel = new JLabel();
        private JLabel sessionLabel = new JLabel();
        private JButton editButton = new JButton("Modifier");
        private JButton deleteButton = new JButton("Supprimer");
        // Enlever la déclaration du printButton ici

        public ExamenListCellRenderer() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));

            // Configuration du panel d'informations
            infoPanel.add(moduleLabel);
            infoPanel.add(dateLabel);
            infoPanel.add(heureLabel);
            infoPanel.add(sessionLabel);

            // Enlever l'ajout du printButton
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);

            add(infoPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.EAST);
            add(separator, BorderLayout.SOUTH);

            // Pour les composants Swing, on utilise java.awt.Font
            java.awt.Font labelFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 12);
            java.awt.Font boldFont = new java.awt.Font("Arial", java.awt.Font.BOLD, 12);

            moduleLabel.setFont(boldFont);
            dateLabel.setFont(labelFont);
            heureLabel.setFont(labelFont);
            sessionLabel.setFont(labelFont);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Examen> list,
                                                      Examen examen, int index, boolean isSelected, boolean cellHasFocus) {

            // Style en fonction de la sélection
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
                infoPanel.setBackground(list.getSelectionBackground());
                buttonPanel.setBackground(list.getSelectionBackground());
            } else {
                setBackground(index % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                setForeground(list.getForeground());
                infoPanel.setBackground(getBackground());
                buttonPanel.setBackground(getBackground());
            }

            // Configuration du contenu
            String moduleName = moduleDAO.findById(examen.getModuleId())
                    .map(Module::getNom)
                    .orElse("N/A");

            moduleLabel.setText("<html><b>Module:</b> " + moduleName + "</html>");
            dateLabel.setText("<html><b>Date:</b> " + examen.getDateExamen() + "</html>");
            heureLabel.setText("<html><b>Horaire:</b> " + examen.getHeureDebut() + " - " +
                    examen.getHeureFin() + "</html>");
            sessionLabel.setText("<html><b>Session:</b> " + examen.getSessionType() + "</html>");

            return this;
        }
    }
    private void filterList(String searchText, int searchType) {
        listModel.clear();
        List<Examen> examens = examenDAO.findByDepartementId(departementId);
        for (Examen examen : examens) {
            boolean match = false;
            if (searchType == 0) { // Module
                match = moduleDAO.findById(examen.getModuleId())
                        .map(module -> module.getNom().toLowerCase().contains(searchText.toLowerCase()))
                        .orElse(false);
            } else if (searchType == 1) { // Date
                match = examen.getDateExamen().toString().contains(searchText);
            }
            if (match || searchText.isEmpty()) {
                listModel.addElement(examen);
            }
        }
    }


    // Fonction pour créer le panel des boutons
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addButton = new JButton("Ajouter un examen");
        addButton.addActionListener(e -> showAddExamenDialog());

        JButton refreshButton = new JButton("Actualiser");
        refreshButton.addActionListener(e -> loadExamens());

        JButton printButton = new JButton("Imprimer les convocations");
        printButton.addActionListener(e -> printConvocations());

        panel.add(addButton);
        panel.add(refreshButton);
        panel.add(printButton);

        return panel;
    }

    private void showAddExamenDialog() {
        JDialog dialog = new JDialog(this, "Ajouter un examen", true);
        dialog.setLayout(new GridLayout(0, 2, 5, 5));

        // Composants du formulaire
        List<Module> modules = moduleDAO.findByDepartementId(departementId);
        JComboBox<Module> moduleCombo = new JComboBox<>(modules.toArray(new Module[0]));

        // Configuration des spinners de date et heure
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));

        JSpinner heureDebutSpinner = new JSpinner(new SpinnerDateModel());
        heureDebutSpinner.setEditor(new JSpinner.DateEditor(heureDebutSpinner, "HH:mm"));

        JSpinner heureFinSpinner = new JSpinner(new SpinnerDateModel());
        heureFinSpinner.setEditor(new JSpinner.DateEditor(heureFinSpinner, "HH:mm"));

        // ComboBox pour le type de session
        JComboBox<SessionType> sessionCombo = new JComboBox<>(SessionType.values());
        sessionCombo.setSelectedItem(SessionType.NORMALE);

        // Ajout des composants
        dialog.add(new JLabel("Module:"));
        dialog.add(moduleCombo);

        dialog.add(new JLabel("Type de session:"));
        dialog.add(sessionCombo);

        dialog.add(new JLabel("Date:"));
        dialog.add(dateSpinner);

        dialog.add(new JLabel("Heure début:"));
        dialog.add(heureDebutSpinner);

        dialog.add(new JLabel("Heure fin:"));
        dialog.add(heureFinSpinner);

        // Boutons
        JButton saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> {
            try {
                Module selectedModule = (Module) moduleCombo.getSelectedItem();
                SessionType selectedSession = (SessionType) sessionCombo.getSelectedItem();
                if (selectedModule != null) {
                    // Conversion des dates
                    Date dateValue = (Date) dateSpinner.getValue();
                    Date heureDebutValue = (Date) heureDebutSpinner.getValue();
                    Date heureFinValue = (Date) heureFinSpinner.getValue();

                    LocalDate date = dateValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalTime heureDebut = heureDebutValue.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
                    LocalTime heureFin = heureFinValue.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

                    Examen examen = new Examen(
                            selectedModule.getId(),
                            date,
                            heureDebut,
                            heureFin,
                            selectedSession
                    );

                    examenDAO.save(examen);
                    loadExamens();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this,
                            "Examen ajouté avec succès",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Erreur lors de l'ajout: " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Fonction pour supprimer un examen
    private void deleteExamen(int examenId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer cet examen ?",
                "Confirmer la suppression",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            examenDAO.delete(examenId);
            loadExamens(); // Recharger la liste
            JOptionPane.showMessageDialog(this,
                    "Examen supprimé avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    private void printConvocations() {
        Examen selected = examenList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un examen",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer la convocation");
        fileChooser.setSelectedFile(new File("convocation_" + selected.getId() + ".pdf"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers PDF (*.pdf)", "pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (!selectedFile.getName().toLowerCase().endsWith(".pdf")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".pdf");
            }

            try {
                Document document = new Document(PageSize.A4);
                PdfWriter.getInstance(document, new FileOutputStream(selectedFile));
                document.open();

                // Marges plus esthétiques
                document.setMargins(50, 50, 50, 50);

                Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24);
                Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
                Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

                // En-tête
                Paragraph title = new Paragraph("Convocation d'Examen", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(30);
                document.add(title);

                Module module = moduleDAO.findById(selected.getModuleId()).orElseThrow(() ->
                        new IllegalStateException("Module non trouvé"));

                // Information de l'examen dans une table avec bordures
                PdfPTable infoTable = new PdfPTable(2);
                infoTable.setWidthPercentage(100);
                infoTable.setSpacingBefore(10);
                infoTable.setSpacingAfter(20);
                float[] columnWidths = {1f, 2f};
                infoTable.setWidths(columnWidths);

                // Style des cellules
                PdfPCell labelCell = new PdfPCell(new Phrase("Module:", boldFont));
                labelCell.setBackgroundColor(new Color(240, 240, 240));
                labelCell.setPadding(8);
                infoTable.addCell(labelCell);

                PdfPCell valueCell = new PdfPCell(new Phrase(module.getNom(), normalFont));
                valueCell.setPadding(8);
                infoTable.addCell(valueCell);

                // Même style pour les autres lignes
                addStyledTableRow(infoTable, "Date:",
                        selected.getDateExamen().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        boldFont, normalFont);

                addStyledTableRow(infoTable, "Horaire:",
                        String.format("%s - %s",
                                selected.getHeureDebut().format(DateTimeFormatter.ofPattern("HH:mm")),
                                selected.getHeureFin().format(DateTimeFormatter.ofPattern("HH:mm"))),
                        boldFont, normalFont);

                addStyledTableRow(infoTable, "Session:",
                        selected.getSessionType() != null ? selected.getSessionType().toString() : "NORMALE",
                        boldFont, normalFont);

                document.add(infoTable);

                // Section des locaux
                LocalDAO localDAO = new LocalDAO();
                List<Local> locaux = localDAO.findAvailableLocalsForExamen(
                        selected.getDateExamen(),
                        selected.getHeureDebut(),
                        selected.getHeureFin()
                );

                Paragraph locauxTitle = new Paragraph("Locaux assignés", boldFont);
                locauxTitle.setSpacingBefore(20);
                locauxTitle.setSpacingAfter(10);
                document.add(locauxTitle);

                if (!locaux.isEmpty()) {
                    PdfPTable locauxTable = new PdfPTable(2);
                    locauxTable.setWidthPercentage(100);

                    // En-têtes
                    PdfPCell headerCell = new PdfPCell(new Phrase("Local", boldFont));
                    headerCell.setBackgroundColor(new Color(240, 240, 240));
                    headerCell.setPadding(5);
                    locauxTable.addCell(headerCell);

                    headerCell = new PdfPCell(new Phrase("Capacité", boldFont));
                    headerCell.setBackgroundColor(new Color(240, 240, 240));
                    headerCell.setPadding(5);
                    locauxTable.addCell(headerCell);

                    for (Local local : locaux) {
                        PdfPCell cell = new PdfPCell(new Phrase(local.getNom(), normalFont));
                        cell.setPadding(5);
                        locauxTable.addCell(cell);

                        cell = new PdfPCell(new Phrase(String.valueOf(local.getCapacite()), normalFont));
                        cell.setPadding(5);
                        locauxTable.addCell(cell);
                    }
                    document.add(locauxTable);
                } else {
                    document.add(new Paragraph("Aucun local assigné", normalFont));
                }

                addSurveillantSection(document, selected, boldFont, normalFont);
                // Pied de page
                Paragraph footer = new Paragraph("\nDocument généré le " +
                        LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        normalFont);
                footer.setAlignment(Element.ALIGN_RIGHT);
                document.add(footer);

                document.close();

                // Proposer d'ouvrir le fichier
                int openFile = JOptionPane.showConfirmDialog(this,
                        "Convocation générée avec succès. Voulez-vous l'ouvrir ?",
                        "Succès",
                        JOptionPane.YES_NO_OPTION);

                if (openFile == JOptionPane.YES_OPTION) {
                    Desktop.getDesktop().open(selectedFile);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la génération de la convocation: " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void addStyledTableRow(PdfPTable table, String label, String value, Font boldFont, Font normalFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, boldFont));
        labelCell.setBackgroundColor(new Color(240, 240, 240));
        labelCell.setPadding(8);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, normalFont));
        valueCell.setPadding(8);
        table.addCell(valueCell);
    }
    // Méthode utilitaire pour ajouter une ligne dans la table
    private void addTableCell(PdfPTable table, String label, String value, Font boldFont, Font normalFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, boldFont));
        labelCell.setBackgroundColor(new Color(240, 240, 240));
        table.addCell(labelCell);
        table.addCell(new Phrase(value, normalFont));
    }
    // Fonction pour modifier un examen
    private void showEditExamenDialog(Examen examen) {
        JDialog dialog = new JDialog(this, "Modifier l'examen", true);
        dialog.setLayout(new GridLayout(0, 2, 5, 5));

        // Composants du formulaire
        List<Module> modules = moduleDAO.findByDepartementId(departementId);
        JComboBox<Module> moduleCombo = new JComboBox<>(modules.toArray(new Module[0]));
        moduleCombo.setSelectedItem(moduleDAO.findById(examen.getModuleId()).orElse(null));

        // Configuration des spinners
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));
        dateSpinner.setValue(Date.from(examen.getDateExamen().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        JSpinner heureDebutSpinner = new JSpinner(new SpinnerDateModel());
        heureDebutSpinner.setEditor(new JSpinner.DateEditor(heureDebutSpinner, "HH:mm"));
        heureDebutSpinner.setValue(Date.from(examen.getHeureDebut().atDate(examen.getDateExamen())
                .atZone(ZoneId.systemDefault()).toInstant()));

        JSpinner heureFinSpinner = new JSpinner(new SpinnerDateModel());
        heureFinSpinner.setEditor(new JSpinner.DateEditor(heureFinSpinner, "HH:mm"));
        heureFinSpinner.setValue(Date.from(examen.getHeureFin().atDate(examen.getDateExamen())
                .atZone(ZoneId.systemDefault()).toInstant()));

        JComboBox<SessionType> sessionCombo = new JComboBox<>(SessionType.values());
        sessionCombo.setSelectedItem(examen.getSessionType());

        // Ajout des composants
        dialog.add(new JLabel("Module:"));
        dialog.add(moduleCombo);
        dialog.add(new JLabel("Type de session:"));
        dialog.add(sessionCombo);
        dialog.add(new JLabel("Date:"));
        dialog.add(dateSpinner);
        dialog.add(new JLabel("Heure début:"));
        dialog.add(heureDebutSpinner);
        dialog.add(new JLabel("Heure fin:"));
        dialog.add(heureFinSpinner);

        // Boutons
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");

        saveButton.addActionListener(e -> {
            try {
                Module selectedModule = (Module) moduleCombo.getSelectedItem();
                SessionType selectedSession = (SessionType) sessionCombo.getSelectedItem();

                if (selectedModule != null) {
                    Date dateValue = (Date) dateSpinner.getValue();
                    Date heureDebutValue = (Date) heureDebutSpinner.getValue();
                    Date heureFinValue = (Date) heureFinSpinner.getValue();

                    LocalDate date = dateValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalTime heureDebut = heureDebutValue.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
                    LocalTime heureFin = heureFinValue.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

                    examen.setModuleId(selectedModule.getId());
                    examen.setDateExamen(date);
                    examen.setHeureDebut(heureDebut);
                    examen.setHeureFin(heureFin);
                    examen.setSessionType(selectedSession);

                    examenDAO.update(examen);
                    loadExamens();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this,
                            "Examen modifié avec succès",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Erreur lors de la modification: " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addSurveillantSection(Document document, Examen selected, Font boldFont, Font normalFont) {
        String query = """
        SELECT 
            s.nom, 
            s.prenom, 
            s.type,
            l.nom as local_nom,
            l.capacite as local_capacite
        FROM examens_surveillants es
        JOIN surveillants s ON es.surveillant_id = s.id
        JOIN locaux l ON es.local_id = l.id
        WHERE es.examen_id = ?
    """;

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, selected.getId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Paragraph survTitle = new Paragraph("\nSurveillants assignés", boldFont);
                survTitle.setSpacingBefore(20);
                survTitle.setSpacingAfter(10);
                document.add(survTitle);

                PdfPTable survTable = new PdfPTable(3);
                survTable.setWidthPercentage(100);

                // En-têtes
                PdfPCell headerCell = new PdfPCell(new Phrase("Surveillant", boldFont));
                headerCell.setBackgroundColor(new Color(240, 240, 240));
                headerCell.setPadding(5);
                survTable.addCell(headerCell);

                headerCell = new PdfPCell(new Phrase("Type", boldFont));
                headerCell.setBackgroundColor(new Color(240, 240, 240));
                headerCell.setPadding(5);
                survTable.addCell(headerCell);

                headerCell = new PdfPCell(new Phrase("Local", boldFont));
                headerCell.setBackgroundColor(new Color(240, 240, 240));
                headerCell.setPadding(5);
                survTable.addCell(headerCell);

                do {
                    // Colonne Surveillant
                    PdfPCell cell = new PdfPCell(new Phrase(
                            rs.getString("nom") + " " + rs.getString("prenom"),
                            normalFont
                    ));
                    cell.setPadding(5);
                    survTable.addCell(cell);

                    // Colonne Type
                    cell = new PdfPCell(new Phrase(rs.getString("type"), normalFont));
                    cell.setPadding(5);
                    survTable.addCell(cell);

                    // Colonne Local
                    cell = new PdfPCell(new Phrase(
                            rs.getString("local_nom") + " (" + rs.getInt("local_capacite") + " places)",
                            normalFont
                    ));
                    cell.setPadding(5);
                    survTable.addCell(cell);
                } while (rs.next());

                document.add(survTable);
            } else {
                Paragraph noSurv = new Paragraph("Aucun surveillant assigné", normalFont);
                noSurv.setSpacingBefore(10);
                document.add(noSurv);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Paragraph error = new Paragraph("Erreur lors de la récupération des surveillants", normalFont);
                error.setSpacingBefore(10);
                document.add(error);
            } catch (DocumentException ex) {
                ex.printStackTrace();
            }
        }
    }
    // Fonction pour créer le panel de recherche
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JComboBox<String> searchType = new JComboBox<>(new String[]{"Module", "Date"});

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { search(); }
            public void removeUpdate(DocumentEvent e) { search(); }
            public void insertUpdate(DocumentEvent e) { search(); }

            private void search() {
                SwingUtilities.invokeLater(() -> {
                    String text = searchField.getText();
                    filterList(text, searchType.getSelectedIndex());
                });
            }
        });

        panel.add(new JLabel("Rechercher par:"));
        panel.add(searchType);
        panel.add(searchField);

        return panel;
    }
}