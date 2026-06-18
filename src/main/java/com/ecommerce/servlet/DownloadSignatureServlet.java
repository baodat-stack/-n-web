package com.ecommerce.servlet;

import java.io.IOException;
import java.util.Base64;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.ecommerce.dao.AuditLogDAO;
import com.ecommerce.dao.OrderDAO;
import com.ecommerce.dao.SignatureDAO;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderSignature;
import com.ecommerce.model.User;
import com.ecommerce.util.AuditAction;

@WebServlet("/user/downloadSignature")
public class DownloadSignatureServlet extends HttpServlet {
    private OrderDAO orderDAO = new OrderDAO();
    private SignatureDAO signatureDAO = new SignatureDAO();
    private AuditLogDAO auditLogDAO = new AuditLogDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp");
            return;
        }

        try {
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            Order order = orderDAO.getOrderById(orderId);
            
            if (order == null || order.getUserId() != currentUser.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                return;
            }

            OrderSignature sig = signatureDAO.getSignatureByOrder(orderId);
            if (sig == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Signature not found");
                return;
            }

            auditLogDAO.log(currentUser.getId(), orderId, AuditAction.DOWNLOAD_SIGNATURE, "Downloaded order signature");

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"order_" + orderId + ".sig\"");
            response.getOutputStream().write(Base64.getEncoder().encode(Base64.getDecoder().decode(sig.getSignatureData())));

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error downloading signature");
        }
    }
}
