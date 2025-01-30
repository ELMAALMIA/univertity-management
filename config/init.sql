-- Création de la base de données
CREATE DATABASE IF NOT EXISTS gestion_examens;
USE gestion_examens;

-- Table des utilisateurs
CREATE TABLE utilisateurs (
                              id INT PRIMARY KEY AUTO_INCREMENT,
                              username VARCHAR(50) UNIQUE NOT NULL,
                              password VARCHAR(255) NOT NULL,
                              role ENUM('ADMIN', 'CHEF_DEPT', 'SURVEILLANT') NOT NULL,
                              email VARCHAR(100) UNIQUE NOT NULL,
                              active BOOLEAN DEFAULT TRUE,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des départements
CREATE TABLE departements (
                              id INT PRIMARY KEY AUTO_INCREMENT,
                              nom VARCHAR(100) NOT NULL UNIQUE
);

-- Table des filières
CREATE TABLE filieres (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          nom VARCHAR(100) NOT NULL,
                          departement_id INT,
                          FOREIGN KEY (departement_id) REFERENCES departements(id) ON DELETE CASCADE
);

-- Table des modules
CREATE TABLE modules (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         nom VARCHAR(100) NOT NULL,
                         semestre INT NOT NULL,
                         filiere_id INT,
                         FOREIGN KEY (filiere_id) REFERENCES filieres(id) ON DELETE CASCADE
);

-- Table des surveillants
CREATE TABLE surveillants (
                              id INT PRIMARY KEY AUTO_INCREMENT,
                              nom VARCHAR(50) NOT NULL,
                              prenom VARCHAR(50) NOT NULL,
                              type ENUM('ENSEIGNANT', 'ADMINISTRATIF') NOT NULL,
                              departement_id INT,
                              user_id INT UNIQUE,
                              FOREIGN KEY (departement_id) REFERENCES departements(id),
                              FOREIGN KEY (user_id) REFERENCES utilisateurs(id)
);

-- Table des locaux
CREATE TABLE locaux (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        nom VARCHAR(50) NOT NULL UNIQUE,
                        capacite INT NOT NULL
);

-- Table des examens
CREATE TABLE examens (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         module_id INT,
                         date_examen DATE NOT NULL,
                         heure_debut TIME NOT NULL,
                         heure_fin TIME NOT NULL,
                         FOREIGN KEY (module_id) REFERENCES modules(id)
);

-- Table de liaison examens-locaux
CREATE TABLE examens_locaux (
                                examen_id INT,
                                local_id INT,
                                PRIMARY KEY (examen_id, local_id),
                                FOREIGN KEY (examen_id) REFERENCES examens(id),
                                FOREIGN KEY (local_id) REFERENCES locaux(id)
);

-- Table de liaison examens-surveillants
CREATE TABLE examens_surveillants (
                                      examen_id INT,
                                      surveillant_id INT,
                                      local_id INT,
                                      PRIMARY KEY (examen_id, surveillant_id),
                                      FOREIGN KEY (examen_id) REFERENCES examens(id),
                                      FOREIGN KEY (surveillant_id) REFERENCES surveillants(id),
                                      FOREIGN KEY (local_id) REFERENCES locaux(id)
);

-- Insertion des départements
INSERT INTO departements (nom) VALUES
                                   ('Informatique'),
                                   ('Mathématiques'),
                                   ('Physique'),
                                   ('Chimie'),
                                   ('Biologie'),
                                   ('Géologie'),
                                   ('Enseignements Transversaux');

-- Insertion des filières
INSERT INTO filieres (nom, departement_id) VALUES
                                               ('Génie Logiciel', 1),
                                               ('Réseaux et Systèmes', 1),
                                               ('Mathématiques Appliquées', 2),
                                               ('Physique des Matériaux', 3),
                                               ('Chimie Organique', 4),
                                               ('Sciences Biologiques', 5),
                                               ('Géologie Appliquée', 6);

-- Insertion des modules
INSERT INTO modules (nom, semestre, filiere_id) VALUES
                                                    ('Programmation Java', 3, 1),
                                                    ('Base de données', 4, 1),
                                                    ('Réseaux', 3, 2),
                                                    ('Systèmes exploitation', 4, 2),
                                                    ('Analyse Numérique', 3, 3),
                                                    ('Physique Quantique', 3, 4),
                                                    ('Chimie Analytique', 3, 5),
                                                    ('Biologie Cellulaire', 3, 6);

-- Insertion des locaux
INSERT INTO locaux (nom, capacite) VALUES
    ('Amphi A', 200),
    ('Amphi B', 150),
    ('Salle 101', 50),
    ('Salle 102', 50),
    ('Salle 103', 40),
    ('Salle 104', 40),
    ('Labo Info 1', 30),
    ('Labo Info 2', 30);

-- Insertion des utilisateurs (avec hash simulé pour les mots de passe)
INSERT INTO utilisateurs (username, password, role, email) VALUES
    ('admin', '$2y$12$hashedAdminPass', 'ADMIN', 'admin@app.com'),
    ('chef_info', '$2y$12$hashedChefInfo', 'CHEF_DEPT', 'chef.info@app.com'),
    ('chef_math', '$2y$12$hashedChefMath', 'CHEF_DEPT', 'chef.math@app.com'),
    ('surveillant1', '$2y$12$hashedSurveillant1', 'SURVEILLANT', 'surveillant1@app.com'),
    ('surveillant2', '$2y$12$hashedSurveillant2', 'SURVEILLANT', 'surveillant2@app.com');

-- Insertion des surveillants
INSERT INTO surveillants (nom, prenom, type, departement_id, user_id) VALUES
    ('Dupont', 'Jean', 'ENSEIGNANT', 1, 3),
    ('Martin', 'Marie', 'ENSEIGNANT', 2, 4),
    ('Dubois', 'Pierre', 'ADMINISTRATIF', 1, NULL),
    ('Laurent', 'Sophie', 'ENSEIGNANT', 1, NULL);

-- Insertion des examens
INSERT INTO examens (module_id, date_examen, heure_debut, heure_fin) VALUES
    (1, '2024-01-15', '09:00:00', '10:30:00'),
    (2, '2024-01-15', '11:00:00', '12:30:00'),
    (3, '2024-01-16', '09:00:00', '10:30:00'),
    (4, '2024-01-16', '11:00:00', '12:30:00');

-- Association examens-locaux
INSERT INTO examens_locaux (examen_id, local_id) VALUES
    (1, 1),
    (1, 2),
    (2, 3),
    (2, 4),
    (3, 5),
    (3, 6),
    (4, 7),
    (4, 8);

-- Association examens-surveillants
INSERT INTO examens_surveillants (examen_id, surveillant_id, local_id) VALUES
    (1, 1, 1),
    (1, 2, 2),
    (2, 3, 3),
    (2, 4, 4),
    (3, 1, 5),
    (3, 2, 6),
    (4, 3, 7),
    (4, 4, 8);
ALTER TABLE utilisateurs ADD COLUMN departement_id INT;
ALTER TABLE utilisateurs ADD FOREIGN KEY (departement_id) REFERENCES departements(id);

-- Mise à jour des utilisateurs existants avec leur département
UPDATE utilisateurs SET departement_id = 1 WHERE username = 'chef_info';
UPDATE utilisateurs SET departement_id = 2 WHERE username = 'chef_math';
--
-- -- Modification de l'insertion des utilisateurs pour inclure le département
-- INSERT INTO utilisateurs (username, password, role, email, departement_id) VALUES
--                                                                                ('admin', '1213456789', 'ADMIN', 'admin@app.com', NULL),
--                                                                                ('chef_info', '1213456789', 'CHEF_DEPT', 'chef.info@app.com', 1),
--                                                                                ('chef_math', '1213456789', 'CHEF_DEPT', 'chef.math@app.com', 2),
--                                                                                ('surveillant1', '1213456789', 'SURVEILLANT', 'surveillant1@app.com', 1),
--                                                                                ('surveillant2', '1213456789', 'SURVEILLANT', 'surveillant2@app.com', 2);


ALTER TABLE examens
    ADD COLUMN session_type ENUM('NORMALE', 'RATTRAPAGE') NOT NULL DEFAULT 'NORMALE';