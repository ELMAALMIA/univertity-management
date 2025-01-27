package com.dev.dao;

import com.dev.enums.Role;
import com.dev.models.Utilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UtilisateurDAO implements DAO<Utilisateur> {
    private final Connection connection;

    public UtilisateurDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public Optional<Utilisateur> findById(Integer id) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM utilisateurs WHERE id = ?"
            );
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Utilisateur user = new Utilisateur(
                        rs.getString("username"),
                        rs.getString("password"),
                        Role.valueOf(rs.getString("role")),
                        rs.getString("email")
                );
                user.setId(rs.getInt("id"));
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Utilisateur> findAll() {
        return List.of();
    }

    @Override
    public Utilisateur save(Utilisateur entity) {
        return null;
    }

    @Override
    public void update(Utilisateur entity) {

    }

    @Override
    public void delete(Integer id) {

    }


}