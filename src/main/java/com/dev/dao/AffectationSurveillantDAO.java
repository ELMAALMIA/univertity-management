package com.dev.dao;


import com.dev.models.AffectationSurveillant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AffectationSurveillantDAO implements DAO<AffectationSurveillant> {

    private final Connection connection;

    public AffectationSurveillantDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    // ✅ Trouver une affectation par ID d'examen
    @Override
    public Optional<com.dev.models.AffectationSurveillant> findById(Integer examenId) {
        String sql = "SELECT * FROM examens_surveillants WHERE examen_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, examenId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                AffectationSurveillant affectation = new AffectationSurveillant(
                        rs.getInt("examen_id"),
                        rs.getInt("surveillant_id"),
                        rs.getInt("local_id")
                );
                return Optional.of(affectation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // ✅ Récupérer toutes les affectations
    @Override
    public List<AffectationSurveillant> findAll() {
        List<AffectationSurveillant> affectations = new ArrayList<>();
        String sql = "SELECT * FROM examens_surveillants";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                affectations.add(new AffectationSurveillant(
                        rs.getInt("examen_id"),
                        rs.getInt("surveillant_id"),
                        rs.getInt("local_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return affectations;
    }

    // ✅ Ajouter une nouvelle affectation
    @Override
    public AffectationSurveillant save(AffectationSurveillant affectation) {
        String sql = "INSERT INTO examens_surveillants (examen_id, surveillant_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, affectation.getExamenId());
            stmt.setInt(2, affectation.getSurveillantId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return affectation;
    }

    // ✅ Mettre à jour une affectation (exemple : changer le surveillant/local)
    @Override
    public void update(AffectationSurveillant affectation) {
        String sql = "UPDATE examens_surveillants SET surveillant_id = ?, local_id = ? WHERE examen_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, affectation.getSurveillantId());
            stmt.setInt(2, 0);
            stmt.setInt(3, affectation.getExamenId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ Supprimer toutes les affectations d’un examen
    @Override
    public void delete(Integer examenId) {
        String sql = "DELETE FROM examens_surveillants WHERE examen_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, examenId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
