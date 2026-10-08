package com.guvi.recommendation.service;

import com.guvi.recommendation.model.Item;
import com.guvi.recommendation.model.Recommendation;
import com.guvi.recommendation.model.SystemSettings;
import com.guvi.recommendation.model.User;
import com.guvi.recommendation.repository.InMemoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RecommendationService {
    private final InMemoryRepository repository;
    private final SystemSettings settings = new SystemSettings();

    public RecommendationService(InMemoryRepository repository) {
        this.repository = repository;
    }

    public List<Recommendation> recommend(int userId) {
        User user = repository.findUser(userId);
        if (user == null) return List.of();

        List<Recommendation> result = new ArrayList<>();
        for (Item item : repository.getItems()) {
            int matches = 0;
            for (String preference : user.getPreferences()) {
                for (String tag : item.getTags()) {
                    if (preference.equalsIgnoreCase(tag)) {
                        matches++;
                        break;
                    }
                }
            }

            double preferenceScore = user.getPreferences().isEmpty()
                    ? 0 : (matches * 100.0 / user.getPreferences().size());
            double ratingScore = item.getRating() / 5.0 * 100;
            double popularityScore = Math.min(100, item.getViews());

            double score = (preferenceScore * settings.getPreferenceWeight()
                    + ratingScore * settings.getRatingWeight()
                    + popularityScore * settings.getPopularityWeight()) / 100.0;

            String reason = matches > 0
                    ? "Matches " + matches + " of your preferred topics"
                    : "Recommended using rating and popularity";
            result.add(new Recommendation(item, Math.round(score * 100.0) / 100.0, reason));
        }

        result.sort(Comparator.comparingDouble(Recommendation::getScore).reversed());
        return result;
    }

    public SystemSettings getSettings() { return settings; }
}
