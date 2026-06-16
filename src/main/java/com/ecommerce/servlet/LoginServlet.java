package com.ecommerce.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.User;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        UserDAO userDAO = new UserDAO();
        User user = userDAO.loginUser(email, password);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("currentUser", user);
            if ("admin".equals(user.getRole())) {
                response.sendRedirect(request.getContextPath() + "/adminDashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/products");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp?error=invalid");
        }
    }
}
