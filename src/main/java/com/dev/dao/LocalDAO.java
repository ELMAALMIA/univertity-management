package com.dev.dao;

import com.dev.models.Local;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LocalDAO implements DAO<Local> {
    private final Connection connection;

    public LocalDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public Optional<Local> findById(Integer id) {
        String sql = "SELECT * FROM locaux WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Local(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("capacite")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du local avec ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Local> findAll() {
        List<Local> locaux = new ArrayList<>();
        String sql = "SELECT * FROM locaux";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                locaux.add(new Local(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("capacite")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de la liste des locaux", e);
        }
        return locaux;
    }

    @Override
    public Local save(Local local) {
        String sql = "INSERT INTO locaux (nom, capacite) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, local.getNom());
            stmt.setInt(2, local.getCapacite());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                local.setId(rs.getInt(1));
            }
            return local;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout du local", e);
        }
    }

    @Override
    public void update(Local local) {
        String sql = "UPDATE locaux SET nom = ?, capacite = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, local.getNom());
            stmt.setInt(2, local.getCapacite());
            stmt.setInt(3, local.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du local avec ID: " + local.getId(), e);
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM locaux WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du local avec ID: " + id, e);
        }
    }


    public List<Local> findAvailableLocalsForExamen(LocalDate date, LocalTime heureDebut, LocalTime heureFin) {
        List<Local> availableLocals = new ArrayList<>();
        String sql = """
        SELECT l.* FROM locaux l
        WHERE l.id NOT IN (
            SELECT el.local_id FROM examens_locaux el
            JOIN examens e ON el.examen_id = e.id
            WHERE e.date_examen = ? AND (
                (e.heure_debut <= ? AND e.heure_fin >= ?) OR
                (e.heure_debut <= ? AND e.heure_fin >= ?) OR
                (e.heure_debut >= ? AND e.heure_fin <= ?)
            )
        )
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            stmt.setTime(2, Time.valueOf(heureDebut));
            stmt.setTime(3, Time.valueOf(heureFin));
            stmt.setTime(4, Time.valueOf(heureDebut));
            stmt.setTime(5, Time.valueOf(heureFin));
            stmt.setTime(6, Time.valueOf(heureDebut));
            stmt.setTime(7, Time.valueOf(heureFin));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                availableLocals.add(new Local(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("capacite")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des locaux disponibles", e);
        }
        return availableLocals;
    }

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

    public List<Local> findByDepartementId(Integer departementId) {
        List<Local> locaux = new ArrayList<>();
        String sql = """
        SELECT l.* FROM locaux l
        JOIN examens_locaux el ON l.id = el.local_id
        JOIN examens e ON el.examen_id = e.id
        JOIN modules m ON e.module_id = m.id
        JOIN filieres f ON m.filiere_id = f.id
        WHERE f.departement_id = ?
        GROUP BY l.id, l.nom, l.capacite
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, departementId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    locaux.add(new Local(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getInt("capacite")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement des locaux du département", e);
        }

        return locaux;
    }
}
