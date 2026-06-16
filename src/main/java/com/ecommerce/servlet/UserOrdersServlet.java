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
import com.ecommerce.model.Order;
import com.ecommerce.model.User;

@WebServlet("/userOrders")
public class UserOrdersServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp");
            return;
        }

        OrderDAO orderDAO = new OrderDAO();
        List<Order> orderList = orderDAO.getUserOrders(currentUser.getId());
        
        request.setAttribute("orderList", orderList);
        request.getRequestDispatcher("user/orders.jsp").forward(request, response);
    }
}
