package com.guvi.recommendation.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String name;
    private String email;
    private List<String> preferences = new ArrayList<>();
    private int interactions;

    public User() {}

    public User(int id, String name, String email, List<String> preferences) {
        this.id = id;
        this.name = name;
        this.email = email;
        if (preferences != null) this.preferences = new ArrayList<>(preferences);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<String> getPreferences() { return preferences; }
    public void setPreferences(List<String> preferences) { this.preferences = preferences; }
    public int getInteractions() { return interactions; }
    public void setInteractions(int interactions) { this.interactions = interactions; }
}
