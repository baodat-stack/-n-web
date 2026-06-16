package com.ecommerce.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.User;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("user");

        UserDAO userDAO = new UserDAO();
        if (userDAO.registerUser(user)) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp?msg=registered");
        } else {
            response.sendRedirect(request.getContextPath() + "/user/register.jsp?error=invalid");
        }
    }
}
