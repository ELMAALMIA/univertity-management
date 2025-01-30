package com.dev.ui.menus;

import com.dev.dao.*;
import com.dev.models.*;
import com.dev.models.Module;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


// Swing et AWT
import java.io.File;

// Java Util

// iText PDF
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
public class StatistiquesUI extends JPanel {
    private final int departementId;
    private final ExamenDAO examenDAO;
    private final SurveillantDAO surveillantDAO;
    private final LocalDAO localDAO;
    private final ModuleDAO moduleDAO;
    private final DepartementDAO departementDAO;
    private  final String depName;

    public StatistiquesUI(int departementId) {
        this.departementId = departementId;
        this.examenDAO = new ExamenDAO();
        this.surveillantDAO = new SurveillantDAO();
        this.localDAO = new LocalDAO();
        this.moduleDAO = new ModuleDAO();


        this.departementDAO = new DepartementDAO();
        Optional<Departement> departement = departementDAO.findById(departementId);
        depName =departement.get().getNom();



        // Configuration du panel
        setLayout(new BorderLayout(10, 10));

        // Créer les onglets
        JTabbedPane tabbedPane = new JTabbedPane();

        // Onglet Examens
        tabbedPane.addTab("Statistiques des Examens", createExamenPanel());

        // Onglet Surveillants
        tabbedPane.addTab("Statistiques des Surveillants", createSurveillantPanel());

        // Onglet Locaux
    tabbedPane.addTab("Statistiques des Locaux", createLocalPanel());

        // Onglet Modules
        tabbedPane.addTab("Statistiques des Modules", createModulePanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Bouton d'exportation
        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Bouton d'exportation CSV
        JButton exportCsvButton = new JButton("Exporter en CSV");
        exportCsvButton.addActionListener(e -> exporterStatistiques());

        // Bouton d'exportation PDF
        JButton exportPdfButton = new JButton("Exporter en PDF");
        exportPdfButton.addActionListener(e -> exporterStatistiquesPDF());

        exportPanel.add(exportCsvButton);
        exportPanel.add(exportPdfButton);

        add(exportPanel, BorderLayout.SOUTH);
    }

    private JPanel createExamenPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Statistiques des examens
        List<Examen> examens = examenDAO.findByDepartementId(departementId);

        String[] colonnes = {"Indicateur", "Valeur"};
        Object[][] donnees = {
                {"Nombre total d'examens", examens.size()},
                {"Nombre de sessions", examens.stream()
                        .map(Examen::getSessionType)
                        .collect(Collectors.toSet()).size()},
                {"Date du premier examen", examens.stream()
                        .map(Examen::getDateExamen)
                        .min(java.util.Comparator.naturalOrder()).orElse(null)},
                {"Date du dernier examen", examens.stream()
                        .map(Examen::getDateExamen)
                        .max(java.util.Comparator.naturalOrder()).orElse(null)}
        };

        JTable table = new JTable(new DefaultTableModel(donnees, colonnes));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSurveillantPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Statistiques des surveillants
        List<Surveillant> surveillants = surveillantDAO.findAll().stream()
                .filter(s -> s.getDepartementId() == departementId)
                .collect(Collectors.toList());

        String[] colonnes = {"Indicateur", "Valeur"};
        Object[][] donnees = {
                {"Nombre total de surveillants", surveillants.size()},
                {"Nombre d'enseignants", surveillants.stream()
                        .filter(s -> s.getType() == com.dev.enums.TypeSurveillant.ENSEIGNANT)
                        .count()},
                {"Nombre de personnel administratif", surveillants.stream()
                        .filter(s -> s.getType() == com.dev.enums.TypeSurveillant.ADMINISTRATIF)
                        .count()}
        };

        JTable table = new JTable(new DefaultTableModel(donnees, colonnes));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLocalPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Statistiques des locaux
        List<Local> locaux = localDAO.findByDepartementId(departementId);

        String[] colonnes = {"Indicateur", "Valeur"};
        Object[][] donnees = {
                {"Nombre total de locaux", locaux.size()},
                {"Capacité totale", locaux.stream()
                        .mapToInt(Local::getCapacite)
                        .sum()},
                {"Capacité moyenne", Math.round(locaux.stream()
                        .mapToInt(Local::getCapacite)
                        .average()
                        .orElse(0) * 100.0) / 100.0}
        };

        JTable table = new JTable(new DefaultTableModel(donnees, colonnes));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createModulePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Statistiques des modules
        List<Module> modules = moduleDAO.findByDepartementId(departementId);

        String[] colonnes = {"Indicateur", "Valeur"};
        Object[][] donnees = {
                {"Nombre total de modules", modules.size()},
                {"Modules par semestre", modules.stream()
                        .collect(Collectors.groupingBy(Module::getSemestre, Collectors.counting()))
                        .toString()}
        };

        JTable table = new JTable(new DefaultTableModel(donnees, colonnes));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private void exporterStatistiques() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter les statistiques");
        fileChooser.setSelectedFile(new java.io.File("Statistiques_Departement.csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            try (java.io.PrintWriter out = new java.io.PrintWriter(fileToSave)) {
                // Écrire l'en-tête
                out.println("Type de Statistique,Indicateur,Valeur");

                // Écrire les statistiques des examens
                out.println("Examens,Nombre total d'examens," +
                        examenDAO.findByDepartementId(departementId).size());

                // Écrire les statistiques des surveillants
                List<Surveillant> surveillants = surveillantDAO.findAll().stream()
                        .filter(s -> s.getDepartementId() == departementId)
                        .collect(Collectors.toList());
                out.println("Surveillants,Nombre total," + surveillants.size());

                // Écrire les statistiques des locaux
                List<Local> locaux = localDAO.findByDepartementId(departementId);
                out.println("Locaux,Nombre total," + locaux.size());
                out.println("Locaux,Capacité totale," +
                        locaux.stream().mapToInt(Local::getCapacite).sum());

                JOptionPane.showMessageDialog(this,
                        "Statistiques exportées avec succès",
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
    private void exporterStatistiquesPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter les statistiques en PDF");
        fileChooser.setSelectedFile(new File("Statistiques_Departement.pdf"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            try {
                // Création du document PDF
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();

                // Titre
                Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
                Paragraph title = new Paragraph("Statistiques du Département "+depName, titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);
                document.add(Chunk.NEWLINE);

                // Statistiques des examens
                ajouterStatistiquesPDF(document, "Statistiques des Examens",
                        examenDAO.findByDepartementId(departementId),
                        this::preparerStatistiquesExamen);

                // Statistiques des surveillants
                List<Surveillant> surveillants = surveillantDAO.findAll().stream()
                        .filter(s -> s.getDepartementId() == departementId)
                        .collect(Collectors.toList());
                ajouterStatistiquesPDF(document, "Statistiques des Surveillants",
                        surveillants,
                        this::preparerStatistiquesSurveillant);

                // Statistiques des locaux
                List<Local> locaux = localDAO.findByDepartementId(departementId);
                ajouterStatistiquesPDF(document, "Statistiques des Locaux",
                        locaux,
                        this::preparerStatistiquesLocal);

                // Statistiques des modules
                List<Module> modules = moduleDAO.findByDepartementId(departementId);
                ajouterStatistiquesPDF(document, "Statistiques des Modules",
                        modules,
                        this::preparerStatistiquesModule);

                document.close();

                JOptionPane.showMessageDialog(this,
                        "Statistiques exportées en PDF avec succès",
                        "Exportation PDF",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'exportation PDF : " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Méthode générique pour ajouter des statistiques au PDF
    private <T> void ajouterStatistiquesPDF(Document document, String titre,
                                            List<T> items,
                                            StatistiqueExtractor<T> extractor)
            throws DocumentException {
        // Titre de la section
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Paragraph sectionTitle = new Paragraph(titre, sectionFont);
        sectionTitle.setAlignment(Element.ALIGN_LEFT);
        document.add(sectionTitle);
        document.add(Chunk.NEWLINE);

        // Création de la table
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.addCell("Indicateur");
        table.addCell("Valeur");

        // Ajout des statistiques
        List<String[]> statistiques = extractor.extraire(items);
        for (String[] stat : statistiques) {
            table.addCell(stat[0]);
            table.addCell(stat[1]);
        }

        document.add(table);
        document.add(Chunk.NEWLINE);
    }

    // Interface fonctionnelle pour extraire les statistiques
    @FunctionalInterface
    private interface StatistiqueExtractor<T> {
        List<String[]> extraire(List<T> items);
    }

    // Méthodes d'extraction spécifiques
    private List<String[]> preparerStatistiquesExamen(List<Examen> examens) {
        return List.of(
                new String[]{"Nombre total d'examens", String.valueOf(examens.size())},
                new String[]{"Nombre de sessions", String.valueOf(
                        examens.stream().map(Examen::getSessionType).collect(Collectors.toSet()).size())},
                new String[]{"Date du premier examen",
                        examens.stream().map(Examen::getDateExamen)
                                .min(java.util.Comparator.naturalOrder())
                                .map(String::valueOf)
                                .orElse("N/A")},
                new String[]{"Date du dernier examen",
                        examens.stream().map(Examen::getDateExamen)
                                .max(java.util.Comparator.naturalOrder())
                                .map(String::valueOf)
                                .orElse("N/A")}
        );
    }

    private List<String[]> preparerStatistiquesSurveillant(List<Surveillant> surveillants) {
        return List.of(
                new String[]{"Nombre total de surveillants", String.valueOf(surveillants.size())},
                new String[]{"Nombre d'enseignants", String.valueOf(
                        surveillants.stream()
                                .filter(s -> s.getType() == com.dev.enums.TypeSurveillant.ENSEIGNANT)
                                .count())},
                new String[]{"Nombre de personnel administratif", String.valueOf(
                        surveillants.stream()
                                .filter(s -> s.getType() == com.dev.enums.TypeSurveillant.ADMINISTRATIF)
                                .count())}
        );
    }

    private List<String[]> preparerStatistiquesLocal(List<Local> locaux) {
        return List.of(
                new String[]{"Nombre total de locaux", String.valueOf(locaux.size())},
                new String[]{"Capacité totale", String.valueOf(
                        locaux.stream().mapToInt(Local::getCapacite).sum())},
                new String[]{"Capacité moyenne", String.format("%.2f",
                        locaux.stream().mapToInt(Local::getCapacite).average().orElse(0))}
        );
    }

    private List<String[]> preparerStatistiquesModule(List<Module> modules) {
        return List.of(
                new String[]{"Nombre total de modules", String.valueOf(modules.size())},
                new String[]{"Modules par semestre", modules.stream()
                        .collect(Collectors.groupingBy(Module::getSemestre, Collectors.counting()))
                        .toString()}
        );
    }

}