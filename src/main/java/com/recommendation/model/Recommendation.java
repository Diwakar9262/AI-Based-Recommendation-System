package com.recommendation.model;

public class Recommendation {
	private int recommendationId;
	private int userId;
	private int productId;
	private double score;
	
	public Recommendation() {
		
	}
	public Recommendation(int recommendationId, int userId, int productId, double score) { 
		this.recommendationId = recommendationId; 
		this.userId = userId; this.productId = productId; 
		this.score = score;
	}
	public int getRecommendationId() { 
		return recommendationId; 
	}
	public void setRecommendationId(int recommendationId) {
		this.recommendationId = recommendationId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) { 
		this.userId = userId;
	}
	public int getProductId() {
		return productId;
	} 
	public void setProductId(int productId) { 
		this.productId = productId;
	}
	public double getScore() { 
		return score;
	} 
	public void setScore(double score) {
		this.score = score; 
	}

}
