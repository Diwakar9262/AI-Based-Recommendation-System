package com.guvi.recommendation.controller;

import com.guvi.recommendation.model.User;
import com.guvi.recommendation.repository.InMemoryRepository;
import com.guvi.recommendation.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {
    private final InMemoryRepository repository;
    private final AuthService authService;

    public AdminController(InMemoryRepository repository, AuthService authService) {
        this.repository = repository;
        this.authService = authService;
    }

    @GetMapping("/stats")
    public Map<String, Object> stats(@RequestHeader(value = "X-Auth-Token", required = false) String token) {
        requireAdmin(token);
        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", repository.getUsers().size());
        data.put("totalItems", repository.getItems().size());
        data.put("totalInteractions", repository.getUsers().stream().mapToInt(User::getInteractions).sum());
        data.put("averageRating", repository.getItems().stream().mapToDouble(i -> i.getRating()).average().orElse(0));
        return data;
    }

    private void requireAdmin(String token) {
        if (!authService.isAdmin(token)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required.");
    }
}
