package com.guvi.recommendation.model;

import java.util.List;

public class Item {
    private int id;
    private String name;
    private String category;
    private double rating;
    private double price;
    private List<String> tags;
    private int views;

    public Item() {}

    public Item(int id, String name, String category, double rating, double price, List<String> tags) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.rating = rating;
        this.price = price;
        this.tags = tags;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }
}
