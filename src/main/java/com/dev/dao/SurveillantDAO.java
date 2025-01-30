package com.dev.dao;

import com.dev.enums.SessionType;
import com.dev.enums.TypeSurveillant; // Assurez-vous d'importer l'énumération
import com.dev.models.Examen;
import com.dev.models.Local;
import com.dev.models.Surveillant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SurveillantDAO implements DAO<Surveillant> {
    private final Connection connection;

    public SurveillantDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public Optional<Surveillant> findById(Integer id) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM surveillants WHERE id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new Surveillant(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        TypeSurveillant.valueOf(rs.getString("type")), // Conversion en enum
                        rs.getInt("departement_id")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du surveillant", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Surveillant> findAll() {
        List<Surveillant> surveillants = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM surveillants");

            while (rs.next()) {
                surveillants.add(new Surveillant(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        TypeSurveillant.valueOf(rs.getString("type")), // Conversion en enum
                        rs.getInt("departement_id")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des surveillants", e);
        }
        return surveillants;
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
            stmt.setString(3, surveillant.getType().toString()); // Type en String
            stmt.setInt(4, surveillant.getDepartementId());

            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                surveillant.setId(generatedKeys.getInt(1));
            }
            return surveillant;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du surveillant", e);
        }
    }

    @Override
    public void update(Surveillant surveillant) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE surveillants SET nom = ?, prenom = ?, type = ?, departement_id = ? WHERE id = ?"
            );
            stmt.setString(1, surveillant.getNom());
            stmt.setString(2, surveillant.getPrenom());
            stmt.setString(3, surveillant.getType().toString()); // Type en String
            stmt.setInt(4, surveillant.getDepartementId());
            stmt.setInt(5, surveillant.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du surveillant", e);
        }
    }

    @Override
    public void delete(Integer id) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM surveillants WHERE id = ?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du surveillant", e);
        }
    }


    // Dans ExamenDAO
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

    // Dans LocalDAO
    public Optional<Local> findByExamenAndSurveillant(int examenId, int surveillantId) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT l.* FROM locaux l " +
                            "JOIN examens_surveillants es ON l.id = es.local_id " +
                            "WHERE es.examen_id = ? AND es.surveillant_id = ?"
            );
            stmt.setInt(1, examenId);
            stmt.setInt(2, surveillantId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Local(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("capacite")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du local", e);
        }
        return Optional.empty();
    }
}
