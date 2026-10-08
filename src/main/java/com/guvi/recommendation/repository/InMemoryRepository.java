package com.guvi.recommendation.repository;

import com.guvi.recommendation.model.Item;
import com.guvi.recommendation.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class InMemoryRepository {
    private final List<User> users = new ArrayList<>();
    private final List<Item> items = new ArrayList<>();

    public InMemoryRepository() {
        users.add(new User(1, "Diwakar", "diwakar@example.com",
                Arrays.asList("java", "programming", "technology")));
        users.add(new User(2, "Aman", "aman@example.com",
                Arrays.asList("database", "web", "programming")));
        users.add(new User(3, "Neha", "neha@example.com",
                Arrays.asList("ai", "technology", "data")));

        items.add(new Item(1, "Java Programming Course", "Education", 4.8, 499,
                Arrays.asList("java", "programming", "backend")));
        items.add(new Item(2, "Spring Boot Guide", "Education", 4.7, 599,
                Arrays.asList("java", "spring", "backend")));
        items.add(new Item(3, "SQL & Database Basics", "Education", 4.6, 399,
                Arrays.asList("sql", "database", "data")));
        items.add(new Item(4, "Web Development Masterclass", "Education", 4.5, 699,
                Arrays.asList("web", "html", "javascript")));
        items.add(new Item(5, "Artificial Intelligence Basics", "Education", 4.9, 799,
                Arrays.asList("ai", "data", "technology")));
        items.add(new Item(6, "Git & GitHub Essentials", "Tools", 4.4, 299,
                Arrays.asList("git", "programming", "tools")));
    }

    public List<User> getUsers() { return users; }
    public List<Item> getItems() { return items; }
    public User findUser(int id) {
        return users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
    }
    public Item findItem(int id) {
        return items.stream().filter(i -> i.getId() == id).findFirst().orElse(null);
    }
}
