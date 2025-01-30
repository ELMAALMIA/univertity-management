package com.dev.dao;

import com.dev.enums.SessionType;
import com.dev.models.Affectation;
import com.dev.models.Examen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AffectationDAO implements DAO<Affectation> {
    private final Connection connection;

    public AffectationDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public Optional<Affectation> findById(Integer id) {
        // Implémentez la logique pour trouver une affectation par ID si nécessaire
        return Optional.empty();
    }

    @Override
    public List<Affectation> findAll() {
        List<Affectation> affectations = new ArrayList<>();
        String query = "SELECT examen_id, local_id FROM examens_locaux";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                int examenId = resultSet.getInt("examen_id");
                int localId = resultSet.getInt("local_id");
                Affectation affectation = new Affectation(examenId, localId);
                affectations.add(affectation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return affectations;
    }

    @Override
    public Affectation save(Affectation affectation) {
        String query = "INSERT INTO examens_locaux (examen_id, local_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, affectation.getExamenId());
            statement.setInt(2, affectation.getLocalId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return affectation;
    }

    @Override
    public void update(Affectation affectation) {
        String query = "UPDATE examens_locaux SET local_id = ? WHERE examen_id = ? AND local_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Nouveau local_id
            statement.setInt(1, affectation.getLocalId());
            // Condition WHERE pour l'ancien examen_id
            statement.setInt(2, affectation.getExamenId());
            // Condition WHERE pour l'ancien local_id
            statement.setInt(3, affectation.getOldLocalId()); // Il faut ajouter cette propriété dans le modèle

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("La mise à jour a échoué, aucune ligne affectée");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la mise à jour: " + e.getMessage());
        }
    }

    @Override
    public void delete(Integer examenId) {
        String query = "DELETE FROM examens_locaux WHERE examen_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, examenId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("La suppression a échoué, aucune ligne affectée");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la suppression: " + e.getMessage());
        }
    }

    public void supprimerAffectation(int examenId, int localId) {
        String query = "DELETE FROM examens_locaux WHERE examen_id = ? AND local_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, examenId);
            statement.setInt(2, localId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Examen> findBySurveillantId(int surveillantId) {
        List<Examen> examens = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT e.* FROM examens e " +
                            "JOIN examens_surveillants es ON e.id = es.examen_id " +
                            "WHERE es.surveillant_id = ?"
            );
            stmt.setInt(1, surveillantId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                examens.add(new Examen(
                        rs.getInt("id"),
                        rs.getInt("module_id"),
                        rs.getDate("date_examen").toLocalDate(),
                        rs.getTime("heure_debut").toLocalTime(),
                        rs.getTime("heure_fin").toLocalTime(),
                        SessionType.valueOf(rs.getString("session_type"))
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des examens", e);
        }
        return examens;
    }
}
