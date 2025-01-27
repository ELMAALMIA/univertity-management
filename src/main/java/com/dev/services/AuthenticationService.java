package com.dev.services;

import com.dev.dao.DatabaseConnection;
import com.dev.enums.Role;
import com.dev.models.Utilisateur;

import java.sql.*;

public class AuthenticationService {
    private Connection connection;

    public AuthenticationService() {
        this.connection = DatabaseConnection.getConnection();
    }

//    public Utilisateur authenticate(String username, String password) throws SQLException {
//        // Prépare la requête SQL pour vérifier les identifiants
//        String query = "SELECT * FROM utilisateurs WHERE username = ?";
//        PreparedStatement stmt = connection.prepareStatement(query);
//        stmt.setString(1, username);
//
//        ResultSet rs = stmt.executeQuery();
//
//        if (rs.next()) {
//            // Vérifie le mot de passe hashé
//            String hashedPassword = SecurityService.hashPassword(password);
//            String storedPassword = rs.getString("password");
//
//            if (hashedPassword.equals(storedPassword)) {
//                // Crée et retourne l'utilisateur si l'authentification réussit
//                Utilisateur user = new Utilisateur(
//                        rs.getString("username"),
//                        "", // On ne stocke pas le mot de passe en mémoire
//                        Role.valueOf(rs.getString("role")),
//                        rs.getString("email")
//
//                );
//
//
//                user.setId(rs.getInt("id"));
//                return user;
//            }
//        }
//
//        return null; // Authentification échouée
//    }



    public Utilisateur authenticate(String username, String password) throws SQLException {
        String query = "SELECT * FROM utilisateurs WHERE username = ? AND password = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, username);
        stmt.setString(2, password); // Comparaison directe pour le moment

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            Utilisateur user = new Utilisateur(
                    rs.getString("username"),
                    "", // On ne stocke pas le mot de passe en mémoire
                    Role.valueOf(rs.getString("role")),
                    rs.getString("email")
            );
            user.setId(rs.getInt("id"));
            return user;
        }

        return null; // Authentification échouée
    }

    // Méthode pour créer un nouvel utilisateur
    public Utilisateur createUser(String username, String password, Role role, String email) throws SQLException {
        // Vérifie si l'utilisateur existe déjà
        String checkQuery = "SELECT COUNT(*) FROM utilisateurs WHERE username = ? OR email = ?";
        PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
        checkStmt.setString(1, username);
        checkStmt.setString(2, email);

        ResultSet rs = checkStmt.executeQuery();
        rs.next();
        if (rs.getInt(1) > 0) {
            throw new SQLException("Un utilisateur avec ce nom ou cet email existe déjà");
        }

        // Hash le mot de passe avant de le stocker
        String hashedPassword = SecurityService.hashPassword(password);

        // Insère le nouvel utilisateur
        String insertQuery = "INSERT INTO utilisateurs (username, password, role, email) VALUES (?, ?, ?, ?)";
        PreparedStatement insertStmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
        insertStmt.setString(1, username);
        insertStmt.setString(2, hashedPassword);
        insertStmt.setString(3, role.name());
        insertStmt.setString(4, email);

        insertStmt.executeUpdate();

        // Récupère l'ID généré
        ResultSet generatedKeys = insertStmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            Utilisateur newUser = new Utilisateur(username, "", role, email);
            newUser.setId(generatedKeys.getInt(1));
            return newUser;
        }

        throw new SQLException("Échec de la création de l'utilisateur");
    }

    // Méthode pour mettre à jour le mot de passe
    public void updatePassword(int userId, String newPassword) throws SQLException {
        String hashedPassword = SecurityService.hashPassword(newPassword);

        String query = "UPDATE utilisateurs SET password = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, hashedPassword);
        stmt.setInt(2, userId);

        stmt.executeUpdate();
    }

    // Méthode pour vérifier si un utilisateur est actif
    public boolean isUserActive(int userId) throws SQLException {
        String query = "SELECT COUNT(*) FROM utilisateurs WHERE id = ? AND active = true";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, userId);

        ResultSet rs = stmt.executeQuery();
        rs.next();
        return rs.getInt(1) > 0;
    }
}