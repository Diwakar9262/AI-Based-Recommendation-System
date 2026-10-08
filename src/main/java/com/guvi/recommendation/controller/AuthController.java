package com.guvi.recommendation.controller;

import com.guvi.recommendation.model.User;
import com.guvi.recommendation.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            return ResponseEntity.badRequest().body(new MessageResponse(false, "Passwords do not match."));
        }
        AuthService.AuthResult result = authService.register(
                request.name(), request.email(), request.password(), request.preferences());
        return result.success()
                ? ResponseEntity.ok(result)
                : ResponseEntity.badRequest().body(new MessageResponse(false, result.message()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        AuthService.AuthResult result = authService.login(request.email(), request.password());
        return result.success()
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(false, result.message()));
    }

    @PostMapping("/logout")
    public MessageResponse logout(@RequestHeader(value = "X-Auth-Token", required = false) String token) {
        authService.logout(token);
        return new MessageResponse(true, "Logged out successfully.");
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader(value = "X-Auth-Token", required = false) String token) {
        if (authService.isAdmin(token)) return ResponseEntity.ok(new MeResponse(true, null));
        User user = authService.getUserFromToken(token);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(false, "Please login first."));
        return ResponseEntity.ok(new MeResponse(false, user));
    }

    public record RegisterRequest(String name, String email, String password, String confirmPassword, String preferences) {}
    public record LoginRequest(String email, String password) {}
    public record MessageResponse(boolean success, String message) {}
    public record MeResponse(boolean admin, User user) {}
}
