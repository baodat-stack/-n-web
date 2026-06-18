<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.ecommerce.model.Order" %>
<%@ page import="com.ecommerce.model.OrderItem" %>
<%@ page import="com.ecommerce.model.Address" %>
<%@ page import="com.ecommerce.model.UserKey" %>
<%@ page import="com.ecommerce.model.OrderSignature" %>
<%@ page import="com.ecommerce.model.User" %>
<%@ page import="com.ecommerce.dao.UserKeyDAO" %>
<%@ page import="com.ecommerce.dao.SignatureDAO" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Order Details - SportSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="../navbar.jsp" />

    <div class="container mt-5 flex-grow-1" style="max-width: 900px;">
        <%
            Order order = (Order) request.getAttribute("order");
            List<OrderItem> items = (List<OrderItem>) request.getAttribute("orderItems");
            Address address = (Address) request.getAttribute("shippingAddress");
        %>

        <% if (order != null) { %>
        <!-- Order Header -->
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-header bg-light d-flex justify-content-between align-items-center py-3">
                <div class="d-flex gap-5">
                    <div>
                        <span class="text-muted small text-uppercase" style="font-size: 11px;">Order Placed</span>
                        <span class="d-block text-dark small fw-bold"><%= order.getOrderDate() %></span>
                    </div>
                    <div>
                        <span class="text-muted small text-uppercase" style="font-size: 11px;">Total</span>
                        <span class="d-block text-dark small fw-bold">$<%= String.format("%.2f", order.getTotalAmount()) %></span>
                    </div>
                    <div>
                        <span class="text-muted small text-uppercase" style="font-size: 11px;">Payment</span>
                        <span class="d-block text-dark small fw-bold"><%= order.getPaymentMethod() != null ? order.getPaymentMethod() : "N/A" %></span>
                    </div>
                </div>
                <div class="text-end">
                    <span class="text-muted small d-block" style="font-size: 11px;">ORDER # <%= order.getId() %></span>
                    <span class="badge bg-<%= "Delivered".equals(order.getStatus()) ? "success" : "warning text-dark" %> px-3 py-2 rounded-1 shadow-sm mt-1"><%= order.getStatus() %></span>
                </div>
            </div>
        </div>

        <div class="row g-4">
            <!-- Order Items -->
            <div class="col-lg-8">
                <div class="card shadow-sm border-0">
                    <div class="card-header bg-white border-bottom pt-3 pb-2">
                        <h5 class="mb-0 fw-bold">📦 Items in this Order</h5>
                    </div>
                    <div class="card-body p-4">
                        <% if (items != null && !items.isEmpty()) {
                            for (OrderItem item : items) {
                                if (item.getProduct() != null) { %>
                                    <div class="row mb-3 pb-3 border-bottom align-items-center">
                                        <div class="col-md-2 text-center">
                                            <img src="<%= item.getProduct().getImage() %>" class="img-fluid" alt="<%= item.getProduct().getName() %>" style="max-height: 80px; object-fit: contain;">
                                        </div>
                                        <div class="col-md-6">
                                            <a href="<%= request.getContextPath() %>/productDetails?id=<%= item.getProduct().getId() %>" class="text-dark text-decoration-none fw-bold"><%= item.getProduct().getName() %></a>
                                            <p class="text-muted small mb-0"><%= item.getProduct().getCategory() %></p>
                                        </div>
                                        <div class="col-md-2 text-center">
                                            <span class="text-muted small">Qty: </span><span class="fw-bold"><%= item.getQuantity() %></span>
                                        </div>
                                        <div class="col-md-2 text-end">
                                            <span class="fw-bold">$<%= String.format("%.2f", item.getPrice() * item.getQuantity()) %></span>
                                            <% if (item.getQuantity() > 1) { %>
                                                <br><span class="text-muted small">$<%= String.format("%.2f", item.getPrice()) %> each</span>
                                            <% } %>
                                        </div>
                                    </div>
                        <%      }
                            }
                        } %>
                        <div class="d-flex justify-content-between align-items-center pt-2">
                            <h5 class="mb-0 fw-bold">Order Total</h5>
                            <h4 class="mb-0 fw-bold text-danger">$<%= String.format("%.2f", order.getTotalAmount()) %></h4>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Shipping Address -->
            <div class="col-lg-4">
                <div class="card shadow-sm border-0 mb-4">
                    <div class="card-header bg-white border-bottom pt-3 pb-2">
                        <h5 class="mb-0 fw-bold">📍 Shipping Address</h5>
                    </div>
                    <div class="card-body p-4">
                        <% if (address != null) { %>
                            <p class="fw-bold mb-1"><%= address.getFullName() %></p>
                            <p class="mb-1"><%= address.getAddressLine() %></p>
                            <p class="mb-1"><%= address.getCity() %> - <%= address.getPincode() %></p>
                            <p class="text-muted small mb-0">Phone: <%= address.getPhone() %></p>
                        <% } else { %>
                            <p class="text-muted mb-0">No address information available.</p>
                        <% } %>
                    </div>
                </div>

                <!-- Digital Signature Block -->
                <div class="card shadow-sm border-0 mb-4">
                    <div class="card-header bg-white border-bottom pt-3 pb-2">
                        <h5 class="mb-0 fw-bold">🔐 Digital Signature</h5>
                    </div>
                    <div class="card-body p-4">
                        <%
                            User currentUser = (User) session.getAttribute("currentUser");
                            SignatureDAO sigDAO = new SignatureDAO();
                            UserKeyDAO keyDAO = new UserKeyDAO();
                            OrderSignature orderSig = sigDAO.getSignatureByOrder(order.getId());
                            boolean hasActiveKey = keyDAO.hasActiveKey(currentUser.getId());
                            
                            String paramVerifyStatus = request.getParameter("verifyStatus");
                            String errorMsg = (String) session.getAttribute("errorMsg");
                            
                            if (paramVerifyStatus != null) {
                                String badge = "secondary";
                                if ("VERIFIED".equals(paramVerifyStatus)) badge = "success";
                                else if ("INVALID".equals(paramVerifyStatus)) badge = "danger";
                                else if ("TAMPERED".equals(paramVerifyStatus)) badge = "warning text-dark";
                                else if ("KEY_REVOKED".equals(paramVerifyStatus)) badge = "dark";
                        %>
                                <div class="alert alert-<%= badge.equals("success") ? "success" : "danger" %> p-2 mb-3 shadow-sm">
                                    Result: <span class="badge bg-<%= badge %>"><%= paramVerifyStatus %></span>
                                </div>
                        <%  } 
                            if (errorMsg != null) { %>
                                <div class="alert alert-danger p-2 mb-3 shadow-sm"><%= errorMsg %></div>
                        <%      session.removeAttribute("errorMsg");
                            }
                        %>

                        <% if (orderSig != null) { 
                            String badge = "secondary";
                            if ("VERIFIED".equals(orderSig.getVerifyStatus())) badge = "success";
                            else if ("INVALID".equals(orderSig.getVerifyStatus())) badge = "danger";
                            else if ("TAMPERED".equals(orderSig.getVerifyStatus())) badge = "warning text-dark";
                            else if ("KEY_REVOKED".equals(orderSig.getVerifyStatus())) badge = "dark";
                        %>
                            <div class="mb-3">
                                <strong>Status:</strong> <span class="badge bg-<%= badge %> px-2 py-1"><%= orderSig.getVerifyStatus() %></span>
                            </div>
                            <p class="small text-muted mb-3">Signed at: <%= orderSig.getSignTime() %></p>
                            
                            <a href="<%= request.getContextPath() %>/user/downloadSignature?orderId=<%= order.getId() %>" class="btn btn-outline-primary btn-sm w-100 rounded-pill mb-2">⬇ Download .sig</a>
                            <a href="<%= request.getContextPath() %>/user/downloadOrderJson?orderId=<%= order.getId() %>" class="btn btn-outline-secondary btn-sm w-100 rounded-pill">⬇ Download .json (Original)</a>

                        <% } else if ("Pending".equalsIgnoreCase(order.getStatus()) || "Processing".equalsIgnoreCase(order.getStatus())) { %>
                            
                            <% if (hasActiveKey) { %>
                                <div class="mb-3">
                                    <p class="small text-muted mb-2">Step 1: Download your order details as JSON</p>
                                    <a href="<%= request.getContextPath() %>/user/downloadOrderJson?orderId=<%= order.getId() %>" class="btn btn-warning btn-sm w-100 rounded-pill fw-bold text-dark mb-3 shadow-sm">⬇ Download order.json</a>
                                    
                                    <p class="small text-muted mb-2">Step 2: Sign it offline using DigitalSignTool</p>
                                    
                                    <p class="small text-muted mb-2">Step 3: Upload the signature (.sig)</p>
                                    <form action="<%= request.getContextPath() %>/user/uploadSignature" method="post" enctype="multipart/form-data">
                                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                        <input type="hidden" name="orderId" value="<%= order.getId() %>">
                                        <div class="mb-2">
                                            <input type="number" name="requestId" class="form-control form-control-sm mb-2" placeholder="Request ID (from JSON)" required>
                                            <input class="form-control form-control-sm" type="file" name="signatureFile" accept=".sig" required>
                                        </div>
                                        <button type="submit" class="btn btn-primary btn-sm w-100 rounded-pill shadow-sm">Upload Signature</button>
                                    </form>
                                </div>
                            <% } else { %>
                                <div class="alert alert-warning p-3 small border-warning">
                                    Bạn chưa có khóa hoạt động để ký.
                                    <br><br>
                                    <a href="<%= request.getContextPath() %>/user/uploadPublicKey" class="btn btn-warning btn-sm fw-bold">Đi tới Quản lý Khóa</a>
                                </div>
                            <% } %>
                            
                        <% } else { %>
                            <div class="alert alert-secondary p-2 small text-center mb-0">
                                Đơn hàng đang ở trạng thái <%= order.getStatus() %> không thể ký.
                            </div>
                        <% } %>
                    </div>
                </div>

                <a href="<%= request.getContextPath() %>/userOrders" class="btn btn-outline-secondary w-100 rounded-pill py-2">← Back to Orders</a>
            </div>
        </div>

        <% } else { %>
            <div class="alert alert-danger">Order not found.</div>
        <% } %>

        <div class="mb-5"></div>
    </div>

    <jsp:include page="../footer.jsp" />
</body>
</html>
