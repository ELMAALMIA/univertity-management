package com.dev.ui;


import com.dev.models.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PlanningExamensUI extends JFrame {
    private JTable examensTable;
    private DefaultTableModel tableModel;
    private List<Examen> examens;

    public PlanningExamensUI(List<Examen> examens) {
        this.examens = examens;

        setTitle("Planning des Examens");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Créer le modèle de table
        String[] columns = {"Date", "Module", "Heure début", "Heure fin", "Local", "Surveillants"};
        tableModel = new DefaultTableModel(columns, 0);
        examensTable = new JTable(tableModel);

        // Remplir la table
        for (Examen examen : examens) {
            Object[] row = {
                    examen.getDate(),
                    examen.getModule().getNom(),
                    examen.getHeureDebut(),
                    examen.getHeureFin(),
                    examen.getLocaux().stream()
                            .map(Local::getNom)
                            .reduce((a, b) -> a + ", " + b)
                            .orElse(""),
                    examen.getSurveillants().stream()
                            .map(Surveillant::getNom)
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("")
            };
            tableModel.addRow(row);
        }

        // Ajouter la table avec scroll
        add(new JScrollPane(examensTable));

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton printButton = new JButton("Imprimer");
        JButton exportButton = new JButton("Exporter PDF");

        buttonPanel.add(printButton);
        buttonPanel.add(exportButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        printButton.addActionListener(e -> printPlanning());
        exportButton.addActionListener(e -> exportToPDF());
    }

    private void printPlanning() {
        try {
            examensTable.print();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'impression : " + e.getMessage());
        }
    }

    private void exportToPDF() {
        // Implémentation de l'export PDF
    }
}