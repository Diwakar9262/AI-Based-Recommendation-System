package com.recommendation.controller;

import com.recommendation.service.RecommendationService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/recommend")
public class RecommendationServlet extends HttpServlet {

    private RecommendationService recommendationService;

    @Override
    public void init() {
        recommendationService = new RecommendationService();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        String userIdParameter = request.getParameter("userId");

        if (userIdParameter == null || userIdParameter.isEmpty()) {
            response.getWriter().println("<h2>Please provide User ID</h2>");
            return;
        }

        int userId = Integer.parseInt(userIdParameter);

        String result = recommendationService.generateRecommendation(userId);

        response.getWriter().println("<h1>AI Recommendation System</h1>");
        response.getWriter().println("<h2>Recommendation</h2>");
        response.getWriter().println("<p>" + result + "</p>");
    }
}