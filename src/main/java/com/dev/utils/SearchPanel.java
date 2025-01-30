package com.dev.utils;


import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class SearchPanel extends JPanel {
    private JTextField searchField;
    private JComboBox<String> filterBox;
    private TableRowSorter<DefaultTableModel> sorter;

    public SearchPanel(JTable table) {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        // Champ de recherche
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(150, 30));

        // Filtre
        filterBox = new JComboBox<>(new String[]{"Tout", "Aujourd'hui",
                "Cette semaine", "Ce mois"});

        // Configuration du sorter
        sorter = new TableRowSorter<>((DefaultTableModel) table.getModel());
        table.setRowSorter(sorter);

        // Écouteur de recherche
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                search();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                search();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                search();
            }

            private void search() {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        // Écouteur de filtre
        filterBox.addActionListener(e -> applyFilter());

        add(new JLabel("Rechercher: "));
        add(searchField);
        add(new JLabel("Filtrer: "));
        add(filterBox);
    }

    private void applyFilter() {
        String filter = (String) filterBox.getSelectedItem();
        if ("Tout".equals(filter)) {
            sorter.setRowFilter(null);
        } else {
            // Implémenter les filtres spécifiques...
        }
    }
}