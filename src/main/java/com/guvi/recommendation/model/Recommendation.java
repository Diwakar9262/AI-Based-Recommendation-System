package com.guvi.recommendation.model;

public class Recommendation {
    private Item item;
    private double score;
    private String reason;

    public Recommendation(Item item, double score, String reason) {
        this.item = item;
        this.score = score;
        this.reason = reason;
    }

    public Item getItem() { return item; }
    public double getScore() { return score; }
    public String getReason() { return reason; }
}
