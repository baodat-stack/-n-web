package com.ecommerce.servlet;

import java.io.IOException;
import java.util.Base64;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import com.ecommerce.model.User;
import com.ecommerce.service.SignatureService;

@WebServlet("/user/uploadSignature")
@MultipartConfig(maxFileSize = 1024 * 1024)
public class UploadSignatureServlet extends HttpServlet {
    private SignatureService signatureService = new SignatureService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp");
            return;
        }

        int orderId = 0;
        try {
            orderId = Integer.parseInt(request.getParameter("orderId"));
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            
            Part filePart = request.getPart("signatureFile");
            if (filePart == null || filePart.getSize() == 0) {
                throw new Exception("Please select a signature file.");
            }

            String base64Sig = new String(filePart.getInputStream().readAllBytes(), "UTF-8").trim();
            byte[] sigBytes = Base64.getDecoder().decode(base64Sig);

            String result = signatureService.verifyAndSaveSignature(orderId, requestId, sigBytes, currentUser.getId());
            
            response.sendRedirect(request.getContextPath() + "/orderDetails?id=" + orderId + "&verifyStatus=" + result);
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/orderDetails?id=" + orderId);
        }
    }
}
