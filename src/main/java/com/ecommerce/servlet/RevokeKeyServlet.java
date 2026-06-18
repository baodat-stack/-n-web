package com.ecommerce.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.ecommerce.model.User;
import com.ecommerce.service.KeyManagementService;

@WebServlet("/user/revokeKey")
public class RevokeKeyServlet extends HttpServlet {
    private KeyManagementService keyService = new KeyManagementService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp");
            return;
        }

        try {
            keyService.revokeUserKey(currentUser.getId());
            response.sendRedirect(request.getContextPath() + "/user/uploadPublicKey?success=Key revoked successfully.");
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/user/uploadPublicKey");
        }
    }
}
