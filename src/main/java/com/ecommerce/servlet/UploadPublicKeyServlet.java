package com.ecommerce.servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import com.ecommerce.dao.UserKeyDAO;
import com.ecommerce.model.User;
import com.ecommerce.service.KeyManagementService;

@WebServlet("/user/uploadPublicKey")
@MultipartConfig(maxFileSize = 1024 * 1024) // 1MB max
public class UploadPublicKeyServlet extends HttpServlet {
    private KeyManagementService keyService = new KeyManagementService();
    private UserKeyDAO userKeyDAO = new UserKeyDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp");
            return;
        }

        request.setAttribute("activeKey", userKeyDAO.getActiveKey(currentUser.getId()));
        request.setAttribute("keyHistory", userKeyDAO.getKeyHistory(currentUser.getId()));
        request.getRequestDispatcher("/user/keyManagement.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp");
            return;
        }

        try {
            Part filePart = request.getPart("publicKeyFile");
            if (filePart == null || filePart.getSize() == 0) {
                throw new Exception("Please select a file to upload.");
            }

            String content = new String(filePart.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            keyService.uploadPublicKey(currentUser.getId(), content);
            
            response.sendRedirect(request.getContextPath() + "/user/uploadPublicKey?success=Key uploaded successfully.");
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/user/uploadPublicKey");
        }
    }
}
