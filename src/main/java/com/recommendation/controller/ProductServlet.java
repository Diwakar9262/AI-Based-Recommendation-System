package com.recommendation.controller;

import com.recommendation.service.ProductService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/product")
public class ProductServlet extends HttpServlet {

    private ProductService productService;

    @Override
    public void init() {
        productService = new ProductService();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        String productIdParameter = request.getParameter("productId");

        if (productIdParameter == null || productIdParameter.isEmpty()) {
            response.getWriter().println("<h2>Please provide Product ID</h2>");
            return;
        }

        int productId = Integer.parseInt(productIdParameter);

        String result = productService.getProductDetails(productId);

        response.getWriter().println("<h1>AI Recommendation System</h1>");
        response.getWriter().println("<h2>Product</h2>");
        response.getWriter().println("<p>" + result + "</p>");
    }
}