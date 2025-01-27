package com.dev.ui;




import com.dev.dao.GestionSurveillants;
import com.dev.models.Surveillant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AjouterSurveillantUI extends JFrame {
    private JTextField nomField;
    private JTextField prenomField;
    private JComboBox<String> typeBox;
    private JComboBox<String> departementBox;
    private GestionSurveillants gestionSurveillants;

    public AjouterSurveillantUI(GestionSurveillants gestionSurveillants) {
        this.gestionSurveillants = gestionSurveillants;

        setTitle("Ajouter un surveillant");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2));

        add(new JLabel("Nom :"));
        nomField = new JTextField();
        add(nomField);

        add(new JLabel("Prénom :"));
        prenomField = new JTextField();
        add(prenomField);

        add(new JLabel("Type :"));
        String[] types = {"Enseignant", "Attaché administratif"};
        typeBox = new JComboBox<>(types);
        add(typeBox);

        add(new JLabel("Département :"));
        String[] departements = {"Informatique", "Mathématiques", "Physique", "Biologie"};
        departementBox = new JComboBox<>(departements);
        add(departementBox);

        JButton addButton = new JButton("Ajouter");
        add(addButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nom = nomField.getText();
                String prenom = prenomField.getText();
                String type = (String) typeBox.getSelectedItem();
                String departement = (String) departementBox.getSelectedItem();

                if (!nom.isEmpty() && !prenom.isEmpty()) {
                    Surveillant surveillant = new Surveillant(nom, prenom, type, departement);
                    gestionSurveillants.ajouterSurveillant(surveillant);
                    JOptionPane.showMessageDialog(null, "Surveillant ajouté !");
                } else {
                    JOptionPane.showMessageDialog(null, "Veuillez remplir tous les champs !");
                }
            }
        });
    }
}