package com.ecommerce.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.ecommerce.dao.CartDAO;
import com.ecommerce.model.Cart;
import com.ecommerce.model.User;

@WebServlet("/addToCart")
public class AddToCartServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp");
            return;
        }

        int productId = Integer.parseInt(request.getParameter("productId"));
        int quantity = 1; // default add 1 item
        
        Cart cart = new Cart();
        cart.setUserId(currentUser.getId());
        cart.setProductId(productId);
        cart.setQuantity(quantity);
        
        CartDAO cartDAO = new CartDAO();
        if (cartDAO.addToCart(cart)) {
            response.sendRedirect(request.getContextPath() + "/cart");
        } else {
            response.sendRedirect(request.getContextPath() + "/products?error=CartFailed");
        }
    }
}
