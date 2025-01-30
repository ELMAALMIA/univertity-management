package com.dev.models;

import com.dev.enums.Role;

public class Utilisateur {
    private Integer id;
    private String username;
    private String password;
    private Role role;
    private String email;
    private boolean active;
    private int departementId;

    public Utilisateur(String username, String password, Role role, String email ) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.active = true;
        this.departementId = departementId;
    }
    public Utilisateur(String username, String password, Role role, String email, int departementId ) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.active = true;
        this.departementId = departementId;
    }


    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setDepartementId(int departementId) {
        this.departementId = departementId;
    }

    public int getDepartementId() {
        return departementId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role.getDisplayName() +
                ", email='" + email + '\'' +
                ", active=" + active +
                '}';
    }
}