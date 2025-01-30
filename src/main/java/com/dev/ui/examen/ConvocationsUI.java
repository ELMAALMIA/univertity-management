//package com.dev.ui.examen;
//
//import com.dev.dao.ExamenDAO;
//import com.dev.dao.LocalDAO;
//import com.dev.dao.ModuleDAO;
//import com.dev.models.Examen;
//import com.dev.models.Local;
//import com.dev.models.Module;
//
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.io.FileOutputStream;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Optional;
//
//public class ConvocationsUI extends JFrame {
//    private final ExamenDAO examenDAO;
//    private final ModuleDAO moduleDAO;
//    private final LocalDAO localDAO;
//    private final int departementId;
//    private JTable examensTable;
//    private DefaultTableModel tableModel;
//    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
//
//    public ConvocationsUI(int departementId) {
//        this.departementId = departementId;
//        this.examenDAO = new ExamenDAO();
//        this.moduleDAO = new ModuleDAO();
//        this.localDAO = new LocalDAO();
//        initializeUI();
//        loadExamensData();
//    }
//
//    private void initializeUI() {
//        setTitle("Gestion des Convocations d'Examens - Département");
//        setSize(1000, 600);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setLocationRelativeTo(null);
//
//        // Création du modèle de table
//        String[] columns = {"ID", "Module", "Date", "Début", "Fin", "Session", "Local"};
//        tableModel = new DefaultTableModel(columns, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return false;
//            }
//        };
//
//        examensTable = new JTable(tableModel);
//        examensTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        JScrollPane scrollPane = new JScrollPane(examensTable);
//
//        // Panneau des boutons
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        JButton generatePdfButton = new JButton("Générer Convocation");
//        generatePdfButton.addActionListener(this::generatePdf);
//        buttonPanel.add(generatePdfButton);
//
//        // Layout principal
//        setLayout(new BorderLayout());
//        add(scrollPane, BorderLayout.CENTER);
//        add(buttonPanel, BorderLayout.SOUTH);
//    }
//
//    private void loadExamensData() {
//        tableModel.setRowCount(0);
//        List<Examen> examens = examenDAO.findByDepartementId(departementId);
//
//        for (Examen examen : examens) {
//            Optional<Module> module = moduleDAO.findById(examen.getModuleId());
//            if (module.isPresent()) {
//                Module m = module.get();
//                // Récupérer les locaux affectés à cet examen
//                List<Local> locaux = localDAO.findAvailableLocalsForExamen(
//                        examen.getDateExamen(),
//                        examen.getHeureDebut(),
//                        examen.getHeureFin()
//                );
//                String locauxStr = locaux.stream()
//                        .map(Local::getNom)
//                        .reduce((a, b) -> a + ", " + b)
//                        .orElse("Non affecté");
//
//                Object[] row = {
//                        examen.getId(),
//                        m.getNom(),
//                        examen.getDateExamen().format(dateFormatter),
//                        examen.getHeureDebut().format(timeFormatter),
//                        examen.getHeureFin().format(timeFormatter),
//                        examen.getSessionType() != null ? examen.getSessionType() : "NORMALE",
//                        locauxStr
//                };
//                tableModel.addRow(row);
//            }
//        }
//    }
//
//    private void generatePdf(ActionEvent e) {
//        int selectedRow = examensTable.getSelectedRow();
//        if (selectedRow == -1) {
//            JOptionPane.showMessageDialog(this,
//                    "Veuillez sélectionner un examen",
//                    "Information",
//                    JOptionPane.INFORMATION_MESSAGE);
//            return;
//        }
//
//        int examenId = (Integer) tableModel.getValueAt(selectedRow, 0);
//        Optional<Examen> optExamen = examenDAO.findById(examenId);
//
//        if (optExamen.isPresent()) {
//            try {
//                Examen examen = optExamen.get();
//                String fileName = "convocation_" + examenId + ".pdf";
//                Document document = new Document();
//                PdfWriter.getInstance(document, new FileOutputStream(fileName));
//                document.open();
//
//                // En-tête avec style
//                Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
//                Paragraph title = new Paragraph("Convocation d'Examen", titleFont);
//                title.setAlignment(Element.ALIGN_CENTER);
//                title.setSpacingAfter(20);
//                document.add(title);
//
//                // Informations de l'examen dans un tableau
//                PdfPTable table = new PdfPTable(2);
//                table.setWidthPercentage(100);
//                table.setSpacingBefore(10);
//                table.setSpacingAfter(10);
//
//                Optional<Module> optModule = moduleDAO.findById(examen.getModuleId());
//                if (optModule.isPresent()) {
//                    Module module = optModule.get();
//
//                    // Style pour les en-têtes de cellules
//                    Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
//
//                    // Ajouter les informations dans le tableau
//                    addTableRow(table, "Module", module.getNom(), headerFont);
//                    addTableRow(table, "Date", examen.getDateExamen().format(dateFormatter), headerFont);
//                    addTableRow(table, "Horaire", examen.getHeureDebut().format(timeFormatter) +
//                            " - " + examen.getHeureFin().format(timeFormatter), headerFont);
//                    addTableRow(table, "Session", examen.getSessionType().toString(), headerFont);
//
//                    document.add(table);
//
//                    // Ajouter la section des locaux
//                    document.add(new Paragraph("\nLocaux assignés:", headerFont));
//                    List<Local> locaux = localDAO.findAvailableLocalsForExamen(
//                            examen.getDateExamen(),
//                            examen.getHeureDebut(),
//                            examen.getHeureFin()
//                    );
//
//                    PdfPTable locauxTable = new PdfPTable(2);
//                    locauxTable.setWidthPercentage(100);
//                    locauxTable.addCell(new PdfPCell(new Phrase("Local", headerFont)));
//                    locauxTable.addCell(new PdfPCell(new Phrase("Capacité", headerFont)));
//
//                    for (Local local : locaux) {
//                        locauxTable.addCell(local.getNom());
//                        locauxTable.addCell(String.valueOf(local.getCapacite()));
//                    }
//                    document.add(locauxTable);
//                }
//
//                document.close();
//                JOptionPane.showMessageDialog(this,
//                        "PDF généré avec succès: " + fileName,
//                        "Succès",
//                        JOptionPane.INFORMATION_MESSAGE);
//
//            } catch (Exception ex) {
//                JOptionPane.showMessageDialog(this,
//                        "Erreur lors de la génération du PDF: " + ex.getMessage(),
//                        "Erreur",
//                        JOptionPane.ERROR_MESSAGE);
//            }
//        }
//    }
//
//    private void addTableRow(PdfPTable table, String label, String value, Font headerFont) {
//        PdfPCell headerCell = new PdfPCell(new Phrase(label, headerFont));
//        headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
//        table.addCell(headerCell);
//        table.addCell(value);
//    }
//
//    public static void show(int departementId) {
//        SwingUtilities.invokeLater(() -> {
//            new ConvocationsUI(departementId).setVisible(true);
//        });
//    }
//}