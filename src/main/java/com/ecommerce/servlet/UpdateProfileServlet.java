package com.ecommerce.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;

import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.User;

@WebServlet("/updateProfile")
public class UpdateProfileServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        UserDAO userDAO = new UserDAO();

        if ("updateName".equals(action)) {
            String newName = request.getParameter("name");
            if (newName != null && !newName.trim().isEmpty()) {
                if (userDAO.updateUserProfile(currentUser.getId(), newName.trim())) {
                    // Update session with new name
                    currentUser.setName(newName.trim());
                    session.setAttribute("currentUser", currentUser);
                    response.sendRedirect(request.getContextPath() + "/profile?msg=nameUpdated");
                } else {
                    response.sendRedirect(request.getContextPath() + "/profile?error=updateFailed");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/profile?error=emptyName");
            }

        } else if ("changePassword".equals(action)) {
            String currentPassword = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");

            // Validate inputs
            if (newPassword == null || newPassword.length() < 6) {
                response.sendRedirect(request.getContextPath() + "/profile?error=shortPassword");
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                response.sendRedirect(request.getContextPath() + "/profile?error=mismatch");
                return;
            }

            // Verify current password
            User freshUser = userDAO.getUserById(currentUser.getId());
            if (!BCrypt.checkpw(currentPassword, freshUser.getPassword())) {
                response.sendRedirect(request.getContextPath() + "/profile?error=wrongPassword");
                return;
            }

            // Update password
            if (userDAO.updateUserPassword(currentUser.getId(), newPassword)) {
                response.sendRedirect(request.getContextPath() + "/profile?msg=passwordChanged");
            } else {
                response.sendRedirect(request.getContextPath() + "/profile?error=updateFailed");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/profile");
        }
    }
}
