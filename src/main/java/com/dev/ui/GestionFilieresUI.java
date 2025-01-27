package com.dev.ui;

import com.dev.dao.FiliereDAO;
import com.dev.models.Departement;
import com.dev.models.Filiere;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GestionFilieresUI extends JFrame {
    private final FiliereDAO filiereDAO;
    private JList<Filiere> filieresList;
    private DefaultListModel<Filiere> listModel;
    private Departement departement;

    // Constructeur sans paramètre
    public GestionFilieresUI() {
        this.filiereDAO = new FiliereDAO();
        initializeUI();
        loadAllFilieres();
    }

    // Constructeur avec département
    public GestionFilieresUI(Departement departement) {
        this.filiereDAO = new FiliereDAO();
        this.departement = departement;
        initializeUI();
        loadFilieresForDepartement();
    }

    private void initializeUI() {
        String title = departement != null ?
                "Filières - " + departement.getNom() : "Gestion des Filières";
        setTitle(title);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Initialisation de la liste
        listModel = new DefaultListModel<>();
        filieresList = new JList<>(listModel);
        filieresList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(filieresList), BorderLayout.CENTER);

        // Panel des boutons
        JPanel buttonsPanel = createButtonsPanel();
        add(buttonsPanel, BorderLayout.SOUTH);

        // Centrer la fenêtre
        setLocationRelativeTo(null);
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addButton = new JButton("Ajouter");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");
        JButton modulesButton = new JButton("Gérer les modules");

        addButton.addActionListener(e -> addFiliere());
        editButton.addActionListener(e -> editFiliere());
        deleteButton.addActionListener(e -> deleteFiliere());
        modulesButton.addActionListener(e -> openModulesManager());

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(modulesButton);

        return buttonsPanel;
    }

    private void loadAllFilieres() {
        try {
            List<Filiere> filieres = filiereDAO.findAll();
            listModel.clear();
            filieres.forEach(listModel::addElement);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des filières: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadFilieresForDepartement() {
        try {
            List<Filiere> filieres = filiereDAO.findByDepartement(departement.getId());
            listModel.clear();
            filieres.forEach(listModel::addElement);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des filières: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addFiliere() {
        String nom = JOptionPane.showInputDialog(this, "Nom de la filière :");
        if (nom != null && !nom.trim().isEmpty()) {
            try {
                Filiere filiere = new Filiere(nom);
                if (departement != null) {
                    filiere.setDepartementId(departement.getId());
                }
                filiere = filiereDAO.save(filiere);
                listModel.addElement(filiere);
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'ajout: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editFiliere() {
        Filiere selected = filieresList.getSelectedValue();
        if (selected != null) {
            String newNom = JOptionPane.showInputDialog(this,
                    "Nouveau nom :", selected.getNom());
            if (newNom != null && !newNom.trim().isEmpty()) {
                try {
                    selected.setNom(newNom);
                    filiereDAO.update(selected);
                    if (departement != null) {
                        loadFilieresForDepartement();
                    } else {
                        loadAllFilieres();
                    }
                } catch (RuntimeException e) {
                    JOptionPane.showMessageDialog(this,
                            "Erreur lors de la modification: " + e.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void deleteFiliere() {
        Filiere selected = filieresList.getSelectedValue();
        if (selected != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Voulez-vous vraiment supprimer cette filière ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    filiereDAO.delete(selected.getId());
                    listModel.removeElement(selected);
                } catch (RuntimeException e) {
                    JOptionPane.showMessageDialog(this,
                            "Erreur lors de la suppression: " + e.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void openModulesManager() {
        Filiere selected = filieresList.getSelectedValue();
        if (selected != null) {
            new GestionModulesUI(selected).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner une filière",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}