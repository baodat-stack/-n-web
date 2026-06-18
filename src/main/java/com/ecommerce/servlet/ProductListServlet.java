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

@WebServlet("/products")
public class ProductListServlet extends HttpServlet {

    private static final int PAGE_SIZE = 8;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductDAO productDAO = new ProductDAO();

        // Get page number (default 1)
        int page = 1;
        String pageStr = request.getParameter("page");
        if (pageStr != null && !pageStr.isEmpty()) {
            try { page = Integer.parseInt(pageStr); } catch (NumberFormatException e) { page = 1; }
            if (page < 1) page = 1;
        }

        // Get category filter (optional)
        String category = request.getParameter("category");
        if (category == null || category.isEmpty()) {
            category = "all";
        }

        List<Product> products;
        int totalProducts;

        if (!"all".equals(category)) {
            // Filter by category
            products = productDAO.getProductsByCategory(category, page, PAGE_SIZE);
            totalProducts = productDAO.getProductCountByCategory(category);
        } else {
            // All products
            products = productDAO.getProductsPaginated(page, PAGE_SIZE);
            totalProducts = productDAO.getProductCount();
        }

        int totalPages = (int) Math.ceil((double) totalProducts / PAGE_SIZE);

        // Get all categories for the filter bar
        List<String> categories = productDAO.getAllCategories();

        request.setAttribute("productList", products);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("selectedCategory", category);
        request.setAttribute("categories", categories);
        request.getRequestDispatcher("products.jsp").forward(request, response);
    }
}
