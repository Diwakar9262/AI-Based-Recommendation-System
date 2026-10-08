package com.guvi.recommendation.controller;

import com.guvi.recommendation.model.Recommendation;
import com.guvi.recommendation.service.AuthService;
import com.guvi.recommendation.service.RecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin
public class RecommendationController {
    private final RecommendationService service;
    private final AuthService authService;

    public RecommendationController(RecommendationService service, AuthService authService) {
        this.service = service;
        this.authService = authService;
    }

    @GetMapping("/{userId}")
    public List<Recommendation> recommendations(@PathVariable int userId,
                                                  @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        if (authService.isAdmin(token)) return service.recommend(userId);
        var user = authService.getUserFromToken(token);
        if (user == null || user.getId() != userId) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own recommendations.");
        return service.recommend(userId);
    }

    @GetMapping("/settings")
    public Object settings(@RequestHeader(value = "X-Auth-Token", required = false) String token) {
        requireAdmin(token);
        return service.getSettings();
    }

    @PutMapping("/settings")
    public Object updateSettings(@RequestBody SettingsRequest request,
                                 @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        requireAdmin(token);
        double total = request.preferenceWeight() + request.ratingWeight() + request.popularityWeight();
        if (request.preferenceWeight() < 0 || request.ratingWeight() < 0 || request.popularityWeight() < 0 || Math.abs(total - 100) > 0.001) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Weights must be non-negative and add up to 100.");
        }
        service.getSettings().setPreferenceWeight(request.preferenceWeight());
        service.getSettings().setRatingWeight(request.ratingWeight());
        service.getSettings().setPopularityWeight(request.popularityWeight());
        return service.getSettings();
    }

    private void requireAdmin(String token) {
        if (!authService.isAdmin(token)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required.");
    }

    public record SettingsRequest(double preferenceWeight, double ratingWeight, double popularityWeight) {}
}
