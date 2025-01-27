package com.dev.models;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class Examen {
    private Module module;
    private Date date;
    private String heureDebut;
    private String heureFin;
    private List<Local> locaux;
    private List<Surveillant> surveillants;

    public Examen(Module module, Date date, String heureDebut, String heureFin) {
        this.module = module;
        this.date = date;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.locaux = new ArrayList<>();
        this.surveillants = new ArrayList<>();
    }

    // Ajouter un local
    public void ajouterLocal(Local local) {
        locaux.add(local);
    }

    // Ajouter un surveillant
    public void ajouterSurveillant(Surveillant surveillant) {
        surveillants.add(surveillant);
    }

    // Getters
    public Module getModule() { return module; }
    public Date getDate() { return date; }
    public String getHeureDebut() { return heureDebut; }
    public String getHeureFin() { return heureFin; }
    public List<Local> getLocaux() { return locaux; }
    public List<Surveillant> getSurveillants() { return surveillants; }
}