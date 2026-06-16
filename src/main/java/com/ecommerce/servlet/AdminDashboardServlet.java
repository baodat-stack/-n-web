package com.ecommerce.servlet;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.ecommerce.dao.OrderDAO;
import com.ecommerce.dao.ProductDAO;
import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;

@WebServlet("/adminDashboard")
public class AdminDashboardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        // Security check (will be replaced by AdminFilter later, but kept for now)
        if (currentUser == null || !"admin".equals(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp");
            return;
        }

        ProductDAO productDAO = new ProductDAO();
        OrderDAO orderDAO = new OrderDAO();
        UserDAO userDAO = new UserDAO();

        // Fetch dashboard data
        List<Product> products = productDAO.getAllProducts();
        int totalProducts = productDAO.getProductCount();
        int totalOrders = orderDAO.getTotalOrderCount();
        double totalRevenue = orderDAO.getTotalRevenue();
        int totalUsers = userDAO.getTotalUserCount();

        // Set attributes
        request.setAttribute("productList", products);
        request.setAttribute("totalProducts", totalProducts);
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("totalUsers", totalUsers);

        request.getRequestDispatcher("admin/adminDashboard.jsp").forward(request, response);
    }
}
