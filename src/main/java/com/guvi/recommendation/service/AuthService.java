package com.guvi.recommendation.service;

import com.guvi.recommendation.model.User;
import com.guvi.recommendation.repository.InMemoryRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    private final InMemoryRepository repository;
    private final Map<String, String> passwordHashes = new ConcurrentHashMap<>();
    private final Map<String, Integer> sessions = new ConcurrentHashMap<>();

    private static final String ADMIN_EMAIL = "admin@airecommend.com";
    private static final String ADMIN_PASSWORD_HASH = hash("Admin@123");

    public AuthService(InMemoryRepository repository) {
        this.repository = repository;
        // Demo accounts for project presentation.
        passwordHashes.put("diwakar@example.com", hash("Diwakar@123"));
        passwordHashes.put("aman@example.com", hash("Aman@123"));
        passwordHashes.put("neha@example.com", hash("Neha@123"));
    }

    public AuthResult register(String name, String email, String password, String preferencesText) {
        name = clean(name);
        email = clean(email).toLowerCase();

        if (name.isBlank()) return AuthResult.error("Name is required.");
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) return AuthResult.error("Enter a valid email address.");
        if (password == null || password.length() < 6) return AuthResult.error("Password must be at least 6 characters.");
        if (findByEmail(email) != null || ADMIN_EMAIL.equals(email)) return AuthResult.error("An account with this email already exists.");

        int id = repository.getUsers().stream().mapToInt(User::getId).max().orElse(0) + 1;
        List<String> preferences = parsePreferences(preferencesText);
        User user = new User(id, name, email, preferences);
        repository.getUsers().add(user);
        passwordHashes.put(email, hash(password));

        String token = createSession(user.getId());
        return AuthResult.success(token, user, "Registration successful.");
    }

    public AuthResult login(String email, String password) {
        email = clean(email).toLowerCase();
        if (ADMIN_EMAIL.equals(email)) {
            if (ADMIN_PASSWORD_HASH.equals(hash(password))) {
                return AuthResult.adminSuccess(createAdminSession(), "Admin login successful.");
            }
            return AuthResult.error("Invalid admin email or password.");
        }

        User user = findByEmail(email);
        if (user == null || !hash(password).equals(passwordHashes.get(email))) {
            return AuthResult.error("Invalid email or password.");
        }

        return AuthResult.success(createSession(user.getId()), user, "Login successful.");
    }

    public void logout(String token) {
        if (token != null) sessions.remove(token);
    }

    public User getUserFromToken(String token) {
        Integer id = token == null ? null : sessions.get(token);
        return id == null || id < 0 ? null : repository.findUser(id);
    }

    public boolean isAdmin(String token) {
        return token != null && sessions.getOrDefault(token, 0) == -1;
    }

    private String createSession(int userId) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, userId);
        return token;
    }

    private String createAdminSession() {
        String token = UUID.randomUUID().toString();
        sessions.put(token, -1);
        return token;
    }

    private User findByEmail(String email) {
        return repository.getUsers().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst().orElse(null);
    }

    private List<String> parsePreferences(String text) {
        List<String> result = new ArrayList<>();
        if (text == null) return result;
        for (String value : text.split(",")) {
            String item = value.trim().toLowerCase();
            if (!item.isBlank() && !result.contains(item)) result.add(item);
        }
        return result;
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private static String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest((value == null ? "" : value).getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) result.append(String.format("%02x", b));
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    public record AuthResult(boolean success, boolean admin, String token, User user, String message) {
        public static AuthResult success(String token, User user, String message) {
            return new AuthResult(true, false, token, user, message);
        }
        public static AuthResult adminSuccess(String token, String message) {
            return new AuthResult(true, true, token, null, message);
        }
        public static AuthResult error(String message) {
            return new AuthResult(false, false, null, null, message);
        }
    }
}
