package com.ecommerce.servlet;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.ecommerce.dao.AddressDAO;
import com.ecommerce.dao.OrderDAO;
import com.ecommerce.model.Address;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.model.User;

@WebServlet("/orderDetails")
public class OrderDetailsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp");
            return;
        }

        int orderId = Integer.parseInt(request.getParameter("id"));

        OrderDAO orderDAO = new OrderDAO();
        Order order = orderDAO.getOrderById(orderId);

        // Security: ensure the user owns this order (or is admin)
        if (order == null || (order.getUserId() != currentUser.getId() && !"admin".equals(currentUser.getRole()))) {
            response.sendRedirect(request.getContextPath() + "/userOrders?error=notFound");
            return;
        }

        List<OrderItem> items = orderDAO.getOrderItems(orderId);

        // Get shipping address if exists
        Address address = null;
        if (order.getAddressId() > 0) {
            AddressDAO addressDAO = new AddressDAO();
            address = addressDAO.getAddressById(order.getAddressId());
        }

        request.setAttribute("order", order);
        request.setAttribute("orderItems", items);
        request.setAttribute("shippingAddress", address);
        request.getRequestDispatcher("user/orderDetails.jsp").forward(request, response);
    }
}
