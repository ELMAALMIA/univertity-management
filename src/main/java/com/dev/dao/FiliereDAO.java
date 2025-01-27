package com.dev.dao;


import com.dev.models.Filiere;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FiliereDAO implements DAO<Filiere> {
    private final Connection connection;

    public FiliereDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public Optional<Filiere> findById(Integer id) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM filieres WHERE id = ?"
            );
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new Filiere(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("departement_id")
                ));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la filière", e);
        }
    }

    @Override
    public List<Filiere> findAll() {
        List<Filiere> filieres = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM filieres");

            while (rs.next()) {
                filieres.add(new Filiere(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("departement_id")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement des filières", e);
        }
        return filieres;
    }

    @Override
    public Filiere save(Filiere filiere) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO filieres (nom, departement_id) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            stmt.setString(1, filiere.getNom());
            stmt.setInt(2, filiere.getDepartementId());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                filiere.setId(rs.getInt(1));
            }
            return filiere;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de la filière", e);
        }
    }

    @Override
    public void update(Filiere filiere) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE filieres SET nom = ?, departement_id = ? WHERE id = ?"
            );
            stmt.setString(1, filiere.getNom());
            stmt.setInt(2, filiere.getDepartementId());
            stmt.setInt(3, filiere.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la filière", e);
        }
    }

    @Override
    public void delete(Integer id) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM filieres WHERE id = ?"
            );
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la filière", e);
        }
    }


    public List<Filiere> findByDepartement(Integer departementId) {
        List<Filiere> filieres = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM filieres WHERE departement_id = ?"
            );
            stmt.setInt(1, departementId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                filieres.add(new Filiere(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("departement_id")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement des filières du département", e);
        }
        return filieres;
    }
}