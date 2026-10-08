package com.guvi.recommendation.controller;

import com.guvi.recommendation.model.User;
import com.guvi.recommendation.service.UserService;
import com.guvi.recommendation.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping
    public List<User> users() {
        return userService.getUsers();
    }

    @PutMapping("/{id}/preferences")
    public User updatePreferences(@PathVariable int id, @RequestBody PreferenceRequest request,
                                  @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        User loggedIn = authService.getUserFromToken(token);
        if (loggedIn == null || loggedIn.getId() != id) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own preferences.");
        }
        return userService.updatePreferences(id, request.preferences());
    }

    public record PreferenceRequest(String preferences) {}
}
