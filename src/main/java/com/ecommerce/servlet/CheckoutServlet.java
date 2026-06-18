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
import com.ecommerce.dao.CartDAO;
import com.ecommerce.dao.OrderDAO;
import com.ecommerce.model.Address;
import com.ecommerce.model.Cart;
import com.ecommerce.model.User;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    // GET: Show checkout page with cart summary + address selection
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp");
            return;
        }

        CartDAO cartDAO = new CartDAO();
        List<Cart> cartList = cartDAO.getCartItemsByUser(currentUser.getId());
        
        if (cartList == null || cartList.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart?error=empty");
            return;
        }

        // Calculate total
        double total = 0;
        for (Cart c : cartList) {
            if (c.getProduct() != null) {
                total += c.getProduct().getPrice() * c.getQuantity();
            }
        }

        // Get user's addresses
        AddressDAO addressDAO = new AddressDAO();
        List<Address> addresses = addressDAO.getAddressesByUser(currentUser.getId());

        request.setAttribute("cartList", cartList);
        request.setAttribute("totalAmount", total);
        request.setAttribute("addressList", addresses);
        request.getRequestDispatcher("user/checkout.jsp").forward(request, response);
    }

    // POST: Place the order
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp");
            return;
        }

        String addressIdParam = request.getParameter("addressId");
        String paymentMethod = request.getParameter("paymentMethod");

        if (addressIdParam == null || addressIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/checkout?error=noAddress");
            return;
        }

        int addressId = Integer.parseInt(addressIdParam);

        CartDAO cartDAO = new CartDAO();
        List<Cart> cartList = cartDAO.getCartItemsByUser(currentUser.getId());
        
        if (cartList == null || cartList.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart?error=empty");
            return;
        }
        
        OrderDAO orderDAO = new OrderDAO();
        if (orderDAO.placeOrder(currentUser.getId(), cartList, addressId, paymentMethod)) {
            response.sendRedirect(request.getContextPath() + "/user/orderSuccess.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + "/checkout?error=checkoutFailed");
        }
    }
}
