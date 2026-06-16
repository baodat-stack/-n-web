package com.ecommerce.servlet;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.ecommerce.dao.ProductDAO;
import com.ecommerce.model.Product;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("q");
        
        ProductDAO productDAO = new ProductDAO();
        List<Product> products = productDAO.searchProducts(query);
        
        request.setAttribute("productList", products);
        request.setAttribute("searchQuery", query);
        request.getRequestDispatcher("products.jsp").forward(request, response);
    }
}
