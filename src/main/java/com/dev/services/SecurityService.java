package com.dev.services;



import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;
import com.dev.models.Utilisateur;

public class SecurityService {
    private static final int SALT_LENGTH = 16;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private static final SecureRandom RANDOM = new SecureRandom();

    // Génère un salt aléatoire
    public static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        return salt;
    }

    // Hash le mot de passe avec un salt
    public static String hashPassword(String password) {
        try {
            byte[] salt = generateSalt();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            byte[] hash = digest.digest(password.getBytes());

            // Combine le salt et le hash pour le stockage
            byte[] combined = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hash, 0, combined, salt.length, hash.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hachage du mot de passe", e);
        }
    }

    // Vérifie si le mot de passe correspond au hash stocké
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            byte[] combined = Base64.getDecoder().decode(storedHash);

            // Extrait le salt et le hash
            byte[] salt = new byte[SALT_LENGTH];
            byte[] hash = new byte[combined.length - SALT_LENGTH];
            System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);
            System.arraycopy(combined, SALT_LENGTH, hash, 0, hash.length);

            // Calcule le hash du mot de passe fourni avec le même salt
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            byte[] newHash = digest.digest(password.getBytes());

            // Compare les hash
            return MessageDigest.isEqual(hash, newHash);
        } catch (NoSuchAlgorithmException | IllegalArgumentException e) {
            return false;
        }
    }

    // Valide la force du mot de passe
    public static boolean isPasswordValid(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    // Valide le format de l'email
    public static boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    // Vérifie les permissions d'un utilisateur
    public static boolean hasPermission(Utilisateur user, String action) {
        if (user == null || user.getRole() == null) {
            return false;
        }

        switch (user.getRole()) {
            case ADMIN:
                return true;
            case CHEF_DEPT:
                return action.startsWith("DEPT_") ||
                        action.equals("VIEW") ||
                        action.equals("MANAGE_SURVEILLANTS");
            case SURVEILLANT:
                return action.equals("VIEW") ||
                        action.equals("VIEW_PLANNING") ||
                        action.equals("UPDATE_PROFILE");
            default:
                return false;
        }
    }

    // Génère un token de réinitialisation de mot de passe
    public static String generateResetToken() {
        byte[] tokenBytes = new byte[32];
        RANDOM.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    // Nettoie les entrées pour prévenir les injections XSS
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&#x27;")
                .replaceAll("&", "&amp;");
    }
}