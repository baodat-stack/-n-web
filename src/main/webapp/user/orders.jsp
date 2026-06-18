<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.ecommerce.model.Order" %>
<%@ page import="com.ecommerce.model.OrderSignature" %>
<%@ page import="com.ecommerce.dao.SignatureDAO" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Your Orders</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="../navbar.jsp" />

    <div class="container mt-5 flex-grow-1" style="max-width: 900px;">
        <h2 class="mb-4 fw-normal">Your Orders</h2>
        
        <% 
            List<Order> orderList = (List<Order>) request.getAttribute("orderList");
            if (orderList != null && !orderList.isEmpty()) {
                for (Order o : orderList) {
        %>
        <div class="card mb-4 border border-light rounded-3 shadow-sm">
            <div class="card-header bg-light d-flex justify-content-between align-items-center py-3 border-bottom">
                <div class="d-flex gap-5">
                    <div>
                        <span class="text-muted small text-uppercase" style="font-size: 11px;">Order Placed</span>
                        <span class="d-block text-dark small"><%= o.getOrderDate() %></span>
                    </div>
                    <div>
                        <span class="text-muted small text-uppercase" style="font-size: 11px;">Total</span>
                        <span class="d-block text-dark small">$<%= String.format("%.2f", o.getTotalAmount()) %></span>
                    </div>
                </div>
                <div class="text-end text-muted small">
                    <span class="d-block" style="font-size: 11px;">ORDER # <%= o.getId() %></span>
                    <a href="<%= request.getContextPath() %>/orderDetails?id=<%= o.getId() %>" class="text-info text-decoration-none">View order details</a>
                </div>
            </div>
            <div class="card-body py-4 px-4">
                <h4 class="card-title fw-bold mb-3"><%= "Processing".equals(o.getStatus()) ? "Arriving soon" : "Status: " + o.getStatus() %></h4>
                <div class="d-flex justify-content-between align-items-center mt-4">
                    <div>
                        <span class="badge bg-<%= "Delivered".equals(o.getStatus()) ? "success" : "warning text-dark" %> px-3 py-2 rounded-1 shadow-sm me-2"><%= o.getStatus() %></span>
                        <% 
                            SignatureDAO sigDAO = new SignatureDAO();
                            OrderSignature os = sigDAO.getSignatureByOrder(o.getId());
                            if (os != null) {
                                String badge = "secondary";
                                if ("VERIFIED".equals(os.getVerifyStatus())) badge = "success";
                                else if ("INVALID".equals(os.getVerifyStatus())) badge = "danger";
                                else if ("TAMPERED".equals(os.getVerifyStatus())) badge = "warning text-dark";
                                else if ("KEY_REVOKED".equals(os.getVerifyStatus())) badge = "dark";
                        %>
                            <span class="badge bg-<%= badge %> px-3 py-2 rounded-1 shadow-sm border border-<%= badge %>" title="Signature: <%= os.getVerifyStatus() %>">🔐 <%= os.getVerifyStatus() %></span>
                        <%  } else { %>
                            <span class="badge bg-light text-secondary border px-3 py-2 rounded-1 shadow-sm">⚪ Unsigned</span>
                        <%  } %>
                    </div>
                    <div>
                        <a href="<%= request.getContextPath() %>/products" class="btn btn-outline-secondary btn-sm rounded-pill px-4 me-2">Buy it again</a>
                        <a href="<%= request.getContextPath() %>/orderDetails?id=<%= o.getId() %>" class="btn btn-outline-secondary btn-sm rounded-pill px-4">Track package</a>
                    </div>
                </div>
            </div>
        </div>
        <% 
                }
            } else {
        %>
        <div class="alert alert-light border p-4 text-center mt-4 shadow-sm">
            <h5 class="text-muted fw-normal mb-3">You have not placed any orders yet.</h5>
            <a href="<%= request.getContextPath() %>/products" class="btn btn-warning rounded-pill px-4">Start shopping</a>
        </div>
        <% } %>
    </div>

    <jsp:include page="../footer.jsp" />
</body>
</html>
