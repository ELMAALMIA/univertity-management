package com.dev.models;

import java.time.LocalDate;

public class Affectation {
    private int id;
    private int examenId;
    private int localId;

    private  int oldLocalId;


    public Affectation(int examenId, int localId) {
        this.examenId = examenId;
        this.localId = localId;
    }


    public int getExamenId() {
        return examenId;
    }

    public int getOldLocalId() {
        return oldLocalId;
    }

    public void setLocalId(int newLocalId) {
        this.oldLocalId = this.localId; // Sauvegarder l'ancien local_id
        this.localId = newLocalId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLocalId() {
        return localId;
    }


    public void setExamenId(int examenId) {
        this.examenId = examenId;
    }


    @Override
    public String toString() {
        return "Affectation{" +
                "examenId=" + examenId +
                ", localId=" + localId +
                '}';
    }
}
