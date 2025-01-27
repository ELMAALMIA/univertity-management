package com.dev.dao;


import com.dev.models.Surveillant;

import java.sql.*;
import java.util.List;
import java.util.Optional;

// DAO pour les surveillants
public class SurveillantDAO implements DAO<Surveillant> {
    private final Connection connection;

    public SurveillantDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public Optional<Surveillant> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public List<Surveillant> findAll() {
        return List.of();
    }

    @Override
    public Surveillant save(Surveillant surveillant) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO surveillants (nom, prenom, type, departement_id) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            stmt.setString(1, surveillant.getNom());
            stmt.setString(2, surveillant.getPrenom());
            stmt.setString(3, surveillant.getType());
            stmt.setInt(4, surveillant.getDepartement().getId());

            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                surveillant.setId(generatedKeys.getInt(1));
            }

            return surveillant;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Surveillant entity) {

    }

    @Override
    public void delete(Integer id) {

    }

    // Autres méthodes implémentées...
}