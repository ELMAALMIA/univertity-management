package com.dev.models;

public class Convocation {
    private int id;
    private Examen examen;
    private Surveillant surveillant;
    private Local local;

    public Convocation(int id, Examen examen, Surveillant surveillant, Local local) {
        this.id = id;
        this.examen = examen;
        this.surveillant = surveillant;
        this.local = local;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Examen getExamen() {
        return examen;
    }

    public void setExamen(Examen examen) {
        this.examen = examen;
    }

    public Surveillant getSurveillant() {
        return surveillant;
    }

    public void setSurveillant(Surveillant surveillant) {
        this.surveillant = surveillant;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }
}