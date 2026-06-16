package com.ecommerce.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.ecommerce.dao.OrderDAO;
import com.ecommerce.model.User;

@WebServlet("/updateOrderStatus")
public class UpdateOrderStatusServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null || !"admin".equals(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp");
            return;
        }

        int orderId = Integer.parseInt(request.getParameter("id"));
        String status = request.getParameter("status");

        OrderDAO orderDAO = new OrderDAO();
        if (orderDAO.updateOrderStatus(orderId, status)) {
            response.sendRedirect(request.getContextPath() + "/adminOrders?msg=StatusUpdated");
        } else {
            response.sendRedirect(request.getContextPath() + "/adminOrders?error=UpdateFailed");
        }
    }
}
