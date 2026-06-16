package com.ecommerce.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.ecommerce.dao.AuditLogDAO;
import com.ecommerce.model.User;

@WebServlet("/admin/auditLogs")
public class AuditLogServlet extends HttpServlet {
    private AuditLogDAO auditLogDAO = new AuditLogDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null || !"admin".equals(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp");
            return;
        }

        String actionType = request.getParameter("actionType");
        request.setAttribute("auditLogs", auditLogDAO.getAll(actionType));
        request.setAttribute("currentFilter", actionType);
        
        request.getRequestDispatcher("/admin/auditLogs.jsp").forward(request, response);
    }
}
