package com.dev.models;

import com.dev.enums.SessionType;

import java.time.LocalDate;
import java.time.LocalTime;

public class Examen {
    private int id;
    private int moduleId;
    private LocalDate dateExamen;
    private LocalTime heureDebut;
    private LocalTime heureFin;

    private SessionType sessionType;

    // Constructeurs, getters et setters
    public Examen(int id, int moduleId, LocalDate dateExamen, LocalTime heureDebut, LocalTime heureFin) {
        this.id = id;
        this.moduleId = moduleId;
        this.dateExamen = dateExamen;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
    }

    public Examen(int moduleId, LocalDate dateExamen, LocalTime heureDebut, LocalTime heureFin) {
        this.moduleId = moduleId;
        this.dateExamen = dateExamen;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
    }



    public Examen(int id,Integer moduleId, LocalDate dateExamen, LocalTime heureDebut, LocalTime heureFin, SessionType sessionType) {
       this.id=id;
        this.moduleId = moduleId;
        this.dateExamen = dateExamen;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.sessionType = sessionType;
    }

    public Examen(Integer moduleId, LocalDate dateExamen, LocalTime heureDebut, LocalTime heureFin, SessionType sessionType) {

        this.moduleId = moduleId;
        this.dateExamen = dateExamen;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.sessionType = sessionType;
    }




    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getModuleId() { return moduleId; }
    public void setModuleId(int moduleId) { this.moduleId = moduleId; }
    public LocalDate getDateExamen() { return dateExamen; }
    public void setDateExamen(LocalDate dateExamen) { this.dateExamen = dateExamen; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }
    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }


    public SessionType getSessionType() {
        return sessionType;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    @Override
    public String toString() {
        return "Examen{" +
                "id=" + id +
                ", moduleId=" + moduleId +
                ", dateExamen=" + dateExamen +
                ", heureDebut=" + heureDebut +
                ", heureFin=" + heureFin +
                ", sessionType=" + sessionType +
                '}';
    }
}