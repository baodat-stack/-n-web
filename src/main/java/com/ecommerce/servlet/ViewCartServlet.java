package com.ecommerce.servlet;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.ecommerce.dao.CartDAO;
import com.ecommerce.model.Cart;
import com.ecommerce.model.User;

@WebServlet("/cart")
public class ViewCartServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp");
            return;
        }

        CartDAO cartDAO = new CartDAO();
        List<Cart> cartList = cartDAO.getCartItemsByUser(currentUser.getId());
        
        request.setAttribute("cartList", cartList);
        
        double total = 0;
        for (Cart c : cartList) {
            if (c.getProduct() != null) {
                total += c.getProduct().getPrice() * c.getQuantity();
            }
        }
        request.setAttribute("totalAmount", total);
        
        request.getRequestDispatcher("user/cart.jsp").forward(request, response);
    }
}
