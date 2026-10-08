package com.guvi.recommendation.service;

import com.guvi.recommendation.model.User;
import com.guvi.recommendation.repository.InMemoryRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final InMemoryRepository repository;

    public UserService(InMemoryRepository repository) {
        this.repository = repository;
    }

    public List<User> getUsers() { return repository.getUsers(); }

    public User updatePreferences(int userId, String preferencesText) {
        User user = repository.findUser(userId);
        if (user == null) return null;

        List<String> preferences = Arrays.stream(preferencesText.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.toList());
        user.setPreferences(preferences);
        return user;
    }
}
