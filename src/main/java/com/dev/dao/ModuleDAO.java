package com.dev.dao;

import com.dev.models.Module;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModuleDAO implements DAO<Module> {
    private final Connection connection;

    public ModuleDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public Optional<Module> findById(Integer id) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM modules WHERE id = ?"
            );
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new Module(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("semestre"),
                        rs.getInt("filiere_id")
                ));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du module", e);
        }
    }

    @Override
    public List<Module> findAll() {
        List<Module> modules = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM modules");

            while (rs.next()) {
                modules.add(new Module(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("semestre"),
                        rs.getInt("filiere_id")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement des modules", e);
        }
        return modules;
    }

    public List<Module> findByDepartementId(Integer departementId) {

        System.out.println(departementId);
        List<Module> modules = new ArrayList<>();
        try {
            String sql = """
                SELECT m.* FROM modules m
                JOIN filieres f ON m.filiere_id = f.id
                WHERE f.departement_id = ?
                """;
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, departementId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                modules.add(new Module(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("semestre"),
                        rs.getInt("filiere_id")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement des modules du département", e);
        }
        return modules;
    }

    @Override
    public Module save(Module module) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO modules (nom, semestre, filiere_id) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            stmt.setString(1, module.getNom());
            stmt.setInt(2, module.getSemestre());
            stmt.setInt(3, module.getFiliereId());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                module.setId(rs.getInt(1));
            }
            return module;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout du module", e);
        }
    }

    @Override
    public void update(Module module) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE modules SET nom = ?, semestre = ?, filiere_id = ? WHERE id = ?"
            );
            stmt.setString(1, module.getNom());
            stmt.setInt(2, module.getSemestre());
            stmt.setInt(3, module.getFiliereId());
            stmt.setInt(4, module.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du module", e);
        }
    }

    @Override
    public void delete(Integer id) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM modules WHERE id = ?"
            );
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du module", e);
        }
    }

    public List<Module> findByFiliereId(Integer filiereId) {
        List<Module> modules = new ArrayList<>();
        try {
            String sql = "SELECT * FROM modules WHERE filiere_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, filiereId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                modules.add(new Module(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("semestre"),
                        rs.getInt("filiere_id")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement des modules de la filière", e);
        }
        return modules;
    }
}