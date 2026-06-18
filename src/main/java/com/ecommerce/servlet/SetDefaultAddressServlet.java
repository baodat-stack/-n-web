package com.ecommerce.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.ecommerce.dao.AddressDAO;
import com.ecommerce.model.User;

@WebServlet("/setDefaultAddress")
public class SetDefaultAddressServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp");
            return;
        }

        int addressId = Integer.parseInt(request.getParameter("id"));
        AddressDAO addressDAO = new AddressDAO();
        addressDAO.setDefaultAddress(currentUser.getId(), addressId);
        
        response.sendRedirect(request.getContextPath() + "/addresses?msg=defaultSet");
    }
}
