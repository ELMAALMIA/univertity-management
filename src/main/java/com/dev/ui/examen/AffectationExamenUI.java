package com.dev.ui.examen;

import com.dev.dao.AffectationDAO;
import com.dev.dao.ExamenDAO;
import com.dev.dao.LocalDAO;
import com.dev.dao.ModuleDAO;
import com.dev.models.Examen;
import com.dev.models.Local;
import com.dev.models.Affectation;
import com.dev.models.Module;
import com.dev.utils.DateLabelFormatter;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.jdatepicker.impl.JDatePanelImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class AffectationExamenUI extends JFrame {
    private final JComboBox<Examen> examenComboBox;
    private final JPanel localPanel;
    private final JTextField searchField;

    private final AffectationListUI parentFrame;
    private JScrollPane scrollPane;

    private static class ExamenComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Examen) {
                Examen examen = (Examen) value;
                ModuleDAO moduleDAO = new ModuleDAO();
                Module module = moduleDAO.findById(examen.getModuleId()).orElse(null);

                String displayText = String.format("%s - Session %s - %s (%s - %s)",
                        module != null ? module.getNom() : "N/A",
                        module != null ? module.getSemestre() : "N/A",
                        examen.getDateExamen(),
                        examen.getHeureDebut(),
                        examen.getHeureFin()
                );
                value = displayText;
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }

    public AffectationExamenUI(AffectationListUI parentFrame) {
        this.parentFrame = parentFrame;
        setTitle("Ajouter une Affectation");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Initialize main panels
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel containing search, date picker and examen selection
        JPanel topPanel = new JPanel(new GridLayout(3, 1, 5, 5));

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Rechercher");
        searchPanel.add(new JLabel("Recherche par Local :"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);



        // Examen Selection Panel
        JPanel examenPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        examenComboBox = new JComboBox<>();
        examenComboBox.setRenderer(new ExamenComboBoxRenderer());
        examenComboBox.setPreferredSize(new Dimension(300, 25));
        examenPanel.add(new JLabel("Sélectionnez un Examen :"));
        examenPanel.add(examenComboBox);

        // Add all panels to top panel
        topPanel.add(searchPanel);

        topPanel.add(examenPanel);

        // Local Panel (Center)
        localPanel = new JPanel();
        localPanel.setLayout(new BoxLayout(localPanel, BoxLayout.Y_AXIS));
        localPanel.setBorder(BorderFactory.createTitledBorder("Liste des Locaux Disponibles"));

        scrollPane = new JScrollPane(localPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(750, 400));

        // Bottom Panel (Buttons)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton assignButton = new JButton("Affecter");
        JButton cancelButton = new JButton("Annuler");
        buttonPanel.add(assignButton);
        buttonPanel.add(cancelButton);

        // Add all main components to the frame
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Add action listeners
        searchButton.addActionListener(this::searchLocals);
        assignButton.addActionListener(this::assignExamsToLocals);
        cancelButton.addActionListener(e -> dispose());

        examenComboBox.addActionListener(this::loadLocalsForSelectedExamen);

        // Load initial data
        loadExamens();

        // Final frame setup
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }


    private boolean isSameDate(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private void loadExamens() {
        ExamenDAO examenDAO = new ExamenDAO();
        List<Examen> examens = examenDAO.findAll();
        examenComboBox.removeAllItems();

        // Trier les examens par date
        examens.sort((e1, e2) -> e1.getDateExamen().compareTo(e2.getDateExamen()));

        for (Examen examen : examens) {
            examenComboBox.addItem(examen);
        }
    }

    private void loadLocalsForSelectedExamen(ActionEvent e) {
        localPanel.removeAll();
        Examen selectedExamen = (Examen) examenComboBox.getSelectedItem();

        if (selectedExamen != null) {
            LocalDAO localDAO = new LocalDAO();
            List<Local> locals = localDAO.findAvailableLocalsForExamen(
                    selectedExamen.getDateExamen(),
                    selectedExamen.getHeureDebut(),
                    selectedExamen.getHeureFin()
            );

            if (locals.isEmpty()) {
                JLabel noLocalsLabel = new JLabel("Aucun local disponible pour cet examen");
                noLocalsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                localPanel.add(noLocalsLabel);
            } else {
                for (Local local : locals) {
                    JPanel localItemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    JCheckBox checkBox = new JCheckBox(String.format("%s (Capacité: %d)",
                            local.getNom(), local.getCapacite()));
                    checkBox.setActionCommand(String.valueOf(local.getId()));
                    localItemPanel.add(checkBox);
                    localPanel.add(localItemPanel);
                }
            }
        }

        localPanel.revalidate();
        localPanel.repaint();
    }

    private void searchLocals(ActionEvent e) {
        String searchText = searchField.getText().toLowerCase();
        LocalDAO localDAO = new LocalDAO();
        List<Local> locals = localDAO.findAll();

        localPanel.removeAll();

        boolean foundLocals = false;
        for (Local local : locals) {
            if (local.getNom().toLowerCase().contains(searchText)) {
                JPanel localItemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JCheckBox checkBox = new JCheckBox(String.format("%s (Capacité: %d)",
                        local.getNom(), local.getCapacite()));
                checkBox.setActionCommand(String.valueOf(local.getId()));
                localItemPanel.add(checkBox);
                localPanel.add(localItemPanel);
                foundLocals = true;
            }
        }

        if (!foundLocals) {
            JLabel noResultsLabel = new JLabel("Aucun local trouvé pour : " + searchText);
            noResultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            localPanel.add(noResultsLabel);
        }

        localPanel.revalidate();
        localPanel.repaint();
    }

    private void assignExamsToLocals(ActionEvent e) {
        Examen selectedExamen = (Examen) examenComboBox.getSelectedItem();
        boolean atLeastOneSelected = false;

        if (selectedExamen != null) {
            Component[] components = localPanel.getComponents();
            for (Component component : components) {
                if (component instanceof JPanel) {
                    Component[] subComponents = ((JPanel) component).getComponents();
                    for (Component subComponent : subComponents) {
                        if (subComponent instanceof JCheckBox) {
                            JCheckBox checkBox = (JCheckBox) subComponent;
                            if (checkBox.isSelected()) {
                                atLeastOneSelected = true;
                                int localId = Integer.parseInt(checkBox.getActionCommand());
                                AffectationDAO affectationDAO = new AffectationDAO();
                                Affectation affectation = new Affectation(selectedExamen.getId(), localId);
                                affectationDAO.save(affectation);
                            }
                        }
                    }
                }
            }

            if (atLeastOneSelected) {
                JOptionPane.showMessageDialog(this,
                        "Affectation réussie !",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                parentFrame.loadAffectations();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Veuillez sélectionner au moins un local.",
                        "Avertissement",
                        JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un examen.",
                    "Avertissement",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}