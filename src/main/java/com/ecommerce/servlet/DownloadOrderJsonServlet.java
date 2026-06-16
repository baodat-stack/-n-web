package com.ecommerce.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.ecommerce.dao.AuditLogDAO;
import com.ecommerce.dao.OrderDAO;
import com.ecommerce.dao.OrderSignRequestDAO;
import com.ecommerce.model.Order;
import com.ecommerce.model.User;
import com.ecommerce.util.AuditAction;
import com.ecommerce.util.OrderJsonBuilder;

@WebServlet("/user/downloadOrderJson")
public class DownloadOrderJsonServlet extends HttpServlet {
    private OrderDAO orderDAO = new OrderDAO();
    private OrderSignRequestDAO signRequestDAO = new OrderSignRequestDAO();
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

            // 1. Create empty request first to get ID
            int requestId = signRequestDAO.save(orderId, currentUser.getId(), "", "");
            if (requestId == -1) throw new Exception("Failed to generate request ID");

            // 2. Build JSON with requestId embedded
            String json = OrderJsonBuilder.buildJson(orderId, requestId);
            String hash = OrderJsonBuilder.computeHash(json);

            // 3. Update request with actual json and hash
            signRequestDAO.updateJson(requestId, json, hash);

            auditLogDAO.log(currentUser.getId(), orderId, AuditAction.DOWNLOAD_JSON, "Downloaded order JSON, requestId: " + requestId);

            response.setContentType("application/json");
            response.setHeader("Content-Disposition", "attachment; filename=\"order_" + orderId + ".json\"");
            response.getOutputStream().write(json.getBytes("UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error downloading JSON");
        }
    }
}
