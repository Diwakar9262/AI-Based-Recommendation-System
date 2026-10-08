package com.guvi.recommendation.model;

public class SystemSettings {
    private double preferenceWeight = 60;
    private double ratingWeight = 25;
    private double popularityWeight = 15;

    public double getPreferenceWeight() { return preferenceWeight; }
    public void setPreferenceWeight(double value) { preferenceWeight = value; }
    public double getRatingWeight() { return ratingWeight; }
    public void setRatingWeight(double value) { ratingWeight = value; }
    public double getPopularityWeight() { return popularityWeight; }
    public void setPopularityWeight(double value) { popularityWeight = value; }
}
