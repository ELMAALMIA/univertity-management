package com.dev.dao;



import com.dev.models.Departement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DepartementDAO implements DAO<Departement> {
    private final Connection connection;

    public DepartementDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public Optional<Departement> findById(Integer id) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM departements WHERE id = ?"
            );
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new Departement(
                        rs.getInt("id"),
                        rs.getString("nom")
                ));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du département", e);
        }
    }

    @Override
    public List<Departement> findAll() {
        List<Departement> departements = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM departements");

            while (rs.next()) {
                departements.add(new Departement(
                        rs.getInt("id"),
                        rs.getString("nom")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement des départements", e);
        }
        return departements;
    }

    @Override
    public Departement save(Departement departement) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO departements (nom) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            stmt.setString(1, departement.getNom());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                departement.setId(rs.getInt(1));
            }
            return departement;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout du département", e);
        }
    }

    @Override
    public void update(Departement departement) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE departements SET nom = ? WHERE id = ?"
            );
            stmt.setString(1, departement.getNom());
            stmt.setInt(2, departement.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du département", e);
        }
    }

    @Override
    public void delete(Integer id) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM departements WHERE id = ?"
            );
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du département", e);
        }
    }
}