package com.dev.dao;

import com.dev.enums.SessionType;
import com.dev.models.Examen;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExamenDAO implements DAO<Examen> {
    private final Connection connection;

    public ExamenDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public Optional<Examen> findById(Integer id) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM examens WHERE id = ?"
            );
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new Examen(
                        rs.getInt("id"),
                        rs.getInt("module_id"),
                        rs.getDate("date_examen").toLocalDate(),
                        rs.getTime("heure_debut").toLocalTime(),
                        rs.getTime("heure_fin").toLocalTime()
                ));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'examen", e);
        }
    }

    @Override
    public List<Examen> findAll() {
        List<Examen> examens = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM examens");

            while (rs.next()) {
                examens.add(new Examen(
                        rs.getInt("id"),
                        rs.getInt("module_id"),
                        rs.getDate("date_examen").toLocalDate(),
                        rs.getTime("heure_debut").toLocalTime(),
                        rs.getTime("heure_fin").toLocalTime()
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement des examens", e);
        }
        return examens;
    }

    public List<Examen> findByDepartementId(Integer departementId) {
        List<Examen> examens = new ArrayList<>();
        try {
            String sql = """
                SELECT e.* FROM examens e 
                JOIN modules m ON e.module_id = m.id 
                JOIN filieres f ON m.filiere_id = f.id 
                WHERE f.departement_id = ?
                """;
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, departementId);
            ResultSet rs = stmt.executeQuery();


                while (rs.next()) {
                    examens.add(new Examen(
                            rs.getInt("id"),
                            rs.getInt("module_id"),
                            rs.getDate("date_examen").toLocalDate(),
                            rs.getTime("heure_debut").toLocalTime(),
                            rs.getTime("heure_fin").toLocalTime(),
                            SessionType.valueOf(rs.getString("session_type").toUpperCase()) // Convertir la chaîne en enum
                    ));


                }
            System.out.println("eee"+examens);



        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement des examens du département", e);
        }
        return examens;
    }

    @Override
    public Examen save(Examen examen) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO examens (module_id, date_examen, heure_debut, heure_fin,session_type) VALUES (?, ?, ?, ?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            stmt.setInt(1, examen.getModuleId());
            stmt.setDate(2, Date.valueOf(examen.getDateExamen()));
            stmt.setTime(3, Time.valueOf(examen.getHeureDebut()));
            stmt.setTime(4, Time.valueOf(examen.getHeureFin()));
            stmt.setString(5,examen.getSessionType().name());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                examen.setId(rs.getInt(1));
            }
            return examen;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de l'examen", e);
        }
    }

    @Override
    public void update(Examen examen) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE examens SET module_id = ?, date_examen = ?, " +
                            "heure_debut = ?, heure_fin = ?, session_type = ? WHERE id = ?"
            );
            stmt.setInt(1, examen.getModuleId());
            stmt.setDate(2, Date.valueOf(examen.getDateExamen()));
            stmt.setTime(3, Time.valueOf(examen.getHeureDebut()));
            stmt.setTime(4, Time.valueOf(examen.getHeureFin()));
            stmt.setString(5, examen.getSessionType().name());  // Ajout de la session
            stmt.setInt(6, examen.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'examen", e);
        }
    }

    @Override
    public void delete(Integer id) {

        try {

            connection.setAutoCommit(false);

            try {

                PreparedStatement stmtSurv = connection.prepareStatement(
                        "DELETE FROM examens_surveillants WHERE examen_id = ?"
                );
                stmtSurv.setInt(1, id);
                stmtSurv.executeUpdate();


                PreparedStatement stmtLoc = connection.prepareStatement(
                        "DELETE FROM examens_locaux WHERE examen_id = ?"
                );
                stmtLoc.setInt(1, id);
                stmtLoc.executeUpdate();


                PreparedStatement stmtExam = connection.prepareStatement(
                        "DELETE FROM examens WHERE id = ?"
                );
                stmtExam.setInt(1, id);
                stmtExam.executeUpdate();


                connection.commit();

            } catch (SQLException e) {

                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'examen", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}