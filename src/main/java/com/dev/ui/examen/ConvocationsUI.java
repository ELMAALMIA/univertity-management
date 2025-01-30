package com.dev.ui.examen;

import com.dev.dao.ExamenDAO;
import com.dev.dao.LocalDAO;
import com.dev.dao.ModuleDAO;
import com.dev.dao.DatabaseConnection;
import com.dev.models.Examen;
import com.dev.models.Local;
import com.dev.models.Module;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ConvocationsUI extends JFrame {
    private final ExamenDAO examenDAO;
    private final ModuleDAO moduleDAO;
    private final LocalDAO localDAO;
    private final int departementId;
    private JTable examensTable;
    private DefaultTableModel tableModel;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public ConvocationsUI(int departementId) {
        this.departementId = departementId;
        this.examenDAO = new ExamenDAO();
        this.moduleDAO = new ModuleDAO();
        this.localDAO = new LocalDAO();
        initializeUI();
        loadExamensData();
    }

    private void initializeUI() {
        setTitle("Gestion des Convocations d'Examens");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] columns = {"ID", "Module", "Date", "Début", "Fin", "Session", "Local"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        examensTable = new JTable(tableModel);
        examensTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        examensTable.getTableHeader().setReorderingAllowed(false);

        JButton printButton = new JButton("Imprimer Convocation");
        printButton.addActionListener(e -> generatePdf());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(printButton);

        mainPanel.add(new JScrollPane(examensTable), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadExamensData() {
        tableModel.setRowCount(0);
        List<Examen> examens = examenDAO.findByDepartementId(departementId);

        for (Examen examen : examens) {
            Optional<Module> module = moduleDAO.findById(examen.getModuleId());
            if (module.isPresent()) {
                Module m = module.get();
                Object[] row = {
                        examen.getId(),
                        m.getNom(),
                        examen.getDateExamen().format(dateFormatter),
                        examen.getHeureDebut().format(timeFormatter),
                        examen.getHeureFin().format(timeFormatter),
                        examen.getSessionType(),
                        getLocauxForExamen(examen.getId())
                };
                tableModel.addRow(row);
            }
        }
    }

    private String getLocauxForExamen(int examenId) {
        String query = """
            SELECT l.nom
            FROM examens_locaux el
            JOIN locaux l ON el.local_id = l.id
            WHERE el.examen_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, examenId);
            ResultSet rs = stmt.executeQuery();

            StringBuilder locaux = new StringBuilder();
            while (rs.next()) {
                if (locaux.length() > 0) locaux.append(", ");
                locaux.append(rs.getString("nom"));
            }
            return locaux.length() > 0 ? locaux.toString() : "Non assigné";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur";
        }
    }

    private void generatePdf() {
        int selectedRow = examensTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un examen",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer la convocation");
        int examenId = (Integer) tableModel.getValueAt(selectedRow, 0);
        fileChooser.setSelectedFile(new File("convocation_" + examenId + ".pdf"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers PDF (*.pdf)", "pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (!selectedFile.getName().toLowerCase().endsWith(".pdf")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".pdf");
            }

            try {
                generatePdfDocument(examenId, selectedFile);

                int openFile = JOptionPane.showConfirmDialog(this,
                        "Convocation générée avec succès. Voulez-vous l'ouvrir ?",
                        "Succès",
                        JOptionPane.YES_NO_OPTION);

                if (openFile == JOptionPane.YES_OPTION) {
                    Desktop.getDesktop().open(selectedFile);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la génération du PDF: " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void generatePdfDocument(int examenId, File file) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

        Paragraph title = new Paragraph("Convocation d'Examen", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(30);
        document.add(title);

        Examen examen = examenDAO.findById(examenId).orElseThrow();
        Module module = moduleDAO.findById(examen.getModuleId()).orElseThrow();

        PdfPTable infoTable = createInfoTable(examen, module, boldFont, normalFont);
        document.add(infoTable);

        addLocauxSection(document, examenId, boldFont, normalFont);
        addSurveillantsSection(document, examenId, boldFont, normalFont);

        Paragraph footer = new Paragraph("\nDocument généré le " +
                LocalDate.now().format(dateFormatter),
                normalFont);
        footer.setAlignment(Element.ALIGN_RIGHT);
        document.add(footer);

        document.close();
    }

    private PdfPTable createInfoTable(Examen examen, Module module, Font boldFont, Font normalFont) throws Exception {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(20);

        addTableRow(table, "Module:", module.getNom(), boldFont, normalFont);
        addTableRow(table, "Date:", examen.getDateExamen().format(dateFormatter), boldFont, normalFont);
        addTableRow(table, "Horaire:",
                examen.getHeureDebut().format(timeFormatter) + " - " +
                        examen.getHeureFin().format(timeFormatter), boldFont, normalFont);
        addTableRow(table, "Session:",
                examen.getSessionType() != null ? examen.getSessionType().toString() : "NORMALE",
                boldFont, normalFont);

        return table;
    }

    private void addLocauxSection(Document document, int examenId, Font boldFont, Font normalFont) throws Exception {
        String query = """
            SELECT l.nom, l.capacite
            FROM examens_locaux el
            JOIN locaux l ON el.local_id = l.id
            WHERE el.examen_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, examenId);
            ResultSet rs = stmt.executeQuery();

            Paragraph locauxTitle = new Paragraph("Locaux assignés", boldFont);
            locauxTitle.setSpacingBefore(20);
            locauxTitle.setSpacingAfter(10);
            document.add(locauxTitle);

            if (rs.next()) {
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);

                PdfPCell headerCell = new PdfPCell(new Phrase("Local", boldFont));
                headerCell.setBackgroundColor(new Color(240, 240, 240));
                headerCell.setPadding(5);
                table.addCell(headerCell);

                headerCell = new PdfPCell(new Phrase("Capacité", boldFont));
                headerCell.setBackgroundColor(new Color(240, 240, 240));
                headerCell.setPadding(5);
                table.addCell(headerCell);

                do {
                    table.addCell(new Phrase(rs.getString("nom"), normalFont));
                    table.addCell(new Phrase(String.valueOf(rs.getInt("capacite")), normalFont));
                } while (rs.next());

                document.add(table);
            } else {
                document.add(new Paragraph("Aucun local assigné", normalFont));
            }
        }
    }

    private void addSurveillantsSection(Document document, int examenId, Font boldFont, Font normalFont) throws Exception {
        String query = """
            SELECT s.nom, s.prenom, s.type, l.nom as local_nom
            FROM examens_surveillants es
            JOIN surveillants s ON es.surveillant_id = s.id
            JOIN locaux l ON es.local_id = l.id
            WHERE es.examen_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, examenId);
            ResultSet rs = stmt.executeQuery();

            Paragraph survTitle = new Paragraph("\nSurveillants assignés", boldFont);
            survTitle.setSpacingBefore(20);
            survTitle.setSpacingAfter(10);
            document.add(survTitle);

            if (rs.next()) {
                PdfPTable table = new PdfPTable(3);
                table.setWidthPercentage(100);

                // En-têtes
                String[] headers = {"Surveillant", "Type", "Local"};
                for (String header : headers) {
                    PdfPCell headerCell = new PdfPCell(new Phrase(header, boldFont));
                    headerCell.setBackgroundColor(new Color(240, 240, 240));
                    headerCell.setPadding(5);
                    table.addCell(headerCell);
                }

                do {
                    table.addCell(new Phrase(
                            rs.getString("nom") + " " + rs.getString("prenom"),
                            normalFont));
                    table.addCell(new Phrase(rs.getString("type"), normalFont));
                    table.addCell(new Phrase(rs.getString("local_nom"), normalFont));
                } while (rs.next());

                document.add(table);
            } else {
                document.add(new Paragraph("Aucun surveillant assigné", normalFont));
            }
        }
    }

    private void addTableRow(PdfPTable table, String label, String value, Font boldFont, Font normalFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, boldFont));
        labelCell.setBackgroundColor(new Color(240, 240, 240));
        labelCell.setPadding(8);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, normalFont));
        valueCell.setPadding(8);
        table.addCell(valueCell);
    }

    public static void show(int departementId) {
        SwingUtilities.invokeLater(() -> {
            new ConvocationsUI(departementId).setVisible(true);
        });
    }
}