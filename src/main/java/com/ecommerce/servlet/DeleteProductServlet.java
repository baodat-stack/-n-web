package com.ecommerce.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.ecommerce.dao.ProductDAO;
import com.ecommerce.model.User;

@WebServlet("/deleteProduct")
public class DeleteProductServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null || !"admin".equals(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp");
            return;
        }

        int id = Integer.parseInt(request.getParameter("id"));
        ProductDAO productDAO = new ProductDAO();
        
        if (productDAO.deleteProduct(id)) {
            response.sendRedirect(request.getContextPath() + "/adminDashboard?msg=ProductDeleted");
        } else {
            response.sendRedirect(request.getContextPath() + "/adminDashboard?error=DeleteFailed");
        }
    }
}
