package com.dev.ui.examen;

import com.dev.dao.LocalDAO;
import com.dev.models.Local;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class GestionLocalUI extends JFrame {
    private final LocalDAO localDAO;
    private final DefaultListModel<Local> listModel;
    private final JList<Local> localList;
    private final JTextField searchField;
    private final JComboBox<String> searchOption;

    public GestionLocalUI() {
        localDAO = new LocalDAO();
        listModel = new DefaultListModel<>();
        localList = new JList<>(listModel);
        searchField = new JTextField(15);
        searchOption = new JComboBox<>(new String[]{"Nom", "Capacité"});

        setTitle("Gestion des Locaux");
        setSize(700, 500); // Fenêtre plus grande
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Personnalisation de l'affichage
        localList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        localList.setCellRenderer(new LocalListCellRenderer());

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JScrollPane(localList), BorderLayout.CENTER);

        // Panel des actions (Recherche + Boutons)
        JPanel actionPanel = createActionPanel();
        mainPanel.add(actionPanel, BorderLayout.NORTH);

        // Panel des boutons "Modifier" et "Supprimer"
        JPanel modifyPanel = createModifyPanel();
        mainPanel.add(modifyPanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadLocaux();
        setLocationRelativeTo(null);
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Panel de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterLocaux(); }
            @Override public void removeUpdate(DocumentEvent e) { filterLocaux(); }
            @Override public void changedUpdate(DocumentEvent e) { filterLocaux(); }
        });

        searchPanel.add(new JLabel("Rechercher par :"));
        searchPanel.add(searchOption);
        searchPanel.add(searchField);

        // Panel des boutons "Ajouter" et "Actualiser"
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Ajouter un local");
        addButton.addActionListener(e -> showAddLocalDialog());
        JButton refreshButton = new JButton("Actualiser");
        refreshButton.addActionListener(e -> loadLocaux());

        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createModifyPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton updateButton = new JButton("Modifier");
        updateButton.addActionListener(e -> showUpdateDialog());

        JButton deleteButton = new JButton("Supprimer");
        deleteButton.addActionListener(e -> deleteLocal());

        panel.add(updateButton);
        panel.add(deleteButton);
        return panel;
    }

    private void filterLocaux() {
        String query = searchField.getText().toLowerCase();
        if (query.isEmpty()) {
            loadLocaux();
            return;
        }

        String option = (String) searchOption.getSelectedItem();
        List<Local> locaux = localDAO.findAll();

        List<Local> filtered = locaux.stream()
                .filter(local -> option.equals("Nom") ? local.getNom().toLowerCase().contains(query)
                        : String.valueOf(local.getCapacite()).contains(query))
                .collect(Collectors.toList());

        listModel.clear();
        filtered.forEach(listModel::addElement);
    }

    private void showAddLocalDialog() {
        JDialog dialog = new JDialog(this, "Ajouter un local", true);
        dialog.setLayout(new GridLayout(3, 2, 10, 10));
        dialog.setSize(400, 200);

        JTextField nameField = new JTextField();
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));

        dialog.add(new JLabel("Nom:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Capacité:"));
        dialog.add(capacitySpinner);

        JButton saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> {
            String name = nameField.getText();
            int capacity = (int) capacitySpinner.getValue();
            Local local = new Local(name, capacity);
            localDAO.save(local);
            loadLocaux();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Local ajouté avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showUpdateDialog() {
        Local selectedLocal = localList.getSelectedValue();
        if (selectedLocal == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un local à modifier.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Modifier un local", true);
        dialog.setLayout(new GridLayout(3, 2, 10, 10));
        dialog.setSize(400, 200);

        JTextField nameField = new JTextField(selectedLocal.getNom());
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(selectedLocal.getCapacite(), 1, 1000, 1));

        dialog.add(new JLabel("Nom:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Capacité:"));
        dialog.add(capacitySpinner);

        JButton saveButton = new JButton("Modifier");
        saveButton.addActionListener(e -> {
            selectedLocal.setNom(nameField.getText());
            selectedLocal.setCapacite((int) capacitySpinner.getValue());
            localDAO.update(selectedLocal);
            loadLocaux();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Local modifié avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteLocal() {
        Local selectedLocal = localList.getSelectedValue();
        if (selectedLocal == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un local à supprimer.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer ce local ?"+selectedLocal.getNom(), "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            localDAO.delete(selectedLocal.getId());
            loadLocaux();
            JOptionPane.showMessageDialog(this, "Local supprimé avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadLocaux() {
        listModel.clear();
        List<Local> locaux = localDAO.findAll();
        locaux.forEach(listModel::addElement);
    }

    private static class LocalListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel label = new JLabel();

            if (value instanceof Local local) {
                label.setText("🏠 " + local.getNom() + " | Capacité: " + local.getCapacite());
            }

            label.setOpaque(true);
            label.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            label.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());

            // Ajouter le label au panel
            panel.add(label, BorderLayout.CENTER);

            // Ajouter un séparateur
            JSeparator separator = new JSeparator();
            panel.add(separator, BorderLayout.SOUTH);

            // Ajuster la taille du panel pour le rendre plus visible
            panel.setPreferredSize(new Dimension(0, 30)); // Hauteur du panel
            panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Marges

            return panel;
        }
    }

}
