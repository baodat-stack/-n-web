<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.ecommerce.model.Cart" %>
<%@ page import="com.ecommerce.model.Address" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Checkout - ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="../navbar.jsp" />

    <div class="container mt-5 flex-grow-1" style="max-width: 1000px;">
        <h2 class="mb-4 fw-normal">Checkout</h2>

        <% String error = request.getParameter("error"); %>
        <% if ("noAddress".equals(error)) { %>
            <div class="alert alert-danger">Please select a shipping address before placing your order.</div>
        <% } else if ("checkoutFailed".equals(error)) { %>
            <div class="alert alert-danger">Checkout failed. A product may be out of stock. Please try again.</div>
        <% } %>

        <%
            List<Cart> cartList = (List<Cart>) request.getAttribute("cartList");
            List<Address> addressList = (List<Address>) request.getAttribute("addressList");
            Double totalObj = (Double) request.getAttribute("totalAmount");
            double total = (totalObj != null) ? totalObj : 0.0;
        %>

        <form action="<%= request.getContextPath() %>/checkout" method="post">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <div class="row g-4">
                <!-- Left Column: Address + Payment -->
                <div class="col-lg-7">
                    <!-- Shipping Address Section -->
                    <div class="card shadow-sm border-0 mb-4">
                        <div class="card-header bg-white border-bottom pt-3 pb-2">
                            <h5 class="mb-0 fw-bold">📍 Shipping Address</h5>
                        </div>
                        <div class="card-body p-4">
                            <% if (addressList != null && !addressList.isEmpty()) { %>
                                <% for (Address addr : addressList) { %>
                                    <div class="form-check mb-3 p-3 border rounded-2 bg-light">
                                        <input class="form-check-input" type="radio" name="addressId" value="<%= addr.getId() %>" id="addr<%= addr.getId() %>" <%= addr.isDefault() ? "checked" : "" %> required>
                                        <label class="form-check-label ms-2" for="addr<%= addr.getId() %>">
                                            <strong><%= addr.getFullName() %></strong><br>
                                            <span class="text-dark"><%= addr.getAddressLine() %></span><br>
                                            <span class="text-dark"><%= addr.getCity() %> - <%= addr.getPincode() %></span><br>
                                            <span class="text-muted small">Phone: <%= addr.getPhone() %></span>
                                            <% if (addr.isDefault()) { %>
                                                <span class="badge bg-warning text-dark ms-2">Default</span>
                                            <% } %>
                                        </label>
                                    </div>
                                <% } %>
                            <% } else { %>
                                <div class="alert alert-warning mb-0">
                                    You have no saved addresses. <a href="<%= request.getContextPath() %>/addresses" class="alert-link">Add an address</a> first.
                                </div>
                            <% } %>
                        </div>
                    </div>

                    <!-- Payment Method Section -->
                    <div class="card shadow-sm border-0 mb-4">
                        <div class="card-header bg-white border-bottom pt-3 pb-2">
                            <h5 class="mb-0 fw-bold">💳 Payment Method</h5>
                        </div>
                        <div class="card-body p-4">
                            <div class="form-check mb-3 p-3 border rounded-2 bg-light">
                                <input class="form-check-input" type="radio" name="paymentMethod" value="COD" id="payCOD" checked>
                                <label class="form-check-label ms-2" for="payCOD">
                                    <strong>Cash on Delivery (COD)</strong><br>
                                    <span class="text-muted small">Pay when your order arrives</span>
                                </label>
                            </div>
                            <div class="form-check mb-2 p-3 border rounded-2 bg-light">
                                <input class="form-check-input" type="radio" name="paymentMethod" value="Card" id="payCard">
                                <label class="form-check-label ms-2" for="payCard">
                                    <strong>Credit/Debit Card</strong> <span class="badge bg-secondary">Simulated</span><br>
                                    <span class="text-muted small">Instant payment confirmation</span>
                                </label>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Right Column: Order Summary -->
                <div class="col-lg-5">
                    <div class="card shadow-sm border-0 position-sticky" style="top: 80px;">
                        <div class="card-header bg-white border-bottom pt-3 pb-2">
                            <h5 class="mb-0 fw-bold">📦 Order Summary</h5>
                        </div>
                        <div class="card-body p-4">
                            <% if (cartList != null) {
                                for (Cart c : cartList) {
                                    if (c.getProduct() != null) { %>
                                        <div class="d-flex justify-content-between align-items-start mb-3 pb-2 border-bottom">
                                            <div>
                                                <p class="mb-0 fw-bold small"><%= c.getProduct().getName() %></p>
                                                <p class="mb-0 text-muted small">Qty: <%= c.getQuantity() %></p>
                                            </div>
                                            <span class="fw-bold">$<%= String.format("%.2f", c.getProduct().getPrice() * c.getQuantity()) %></span>
                                        </div>
                            <%      }
                                }
                            } %>

                            <div class="d-flex justify-content-between align-items-center mt-3 pt-2 border-top">
                                <h5 class="mb-0 fw-bold">Total</h5>
                                <h4 class="mb-0 fw-bold text-danger">$<%= String.format("%.2f", total) %></h4>
                            </div>

                            <% if (addressList != null && !addressList.isEmpty()) { %>
                                <button type="submit" class="btn btn-warning w-100 rounded-pill mt-4 py-2 fw-bold shadow-sm" style="background-color: #ffd814;">Place Order</button>
                            <% } else { %>
                                <a href="<%= request.getContextPath() %>/addresses" class="btn btn-warning w-100 rounded-pill mt-4 py-2 fw-bold shadow-sm" style="background-color: #ffd814;">Add Address to Continue</a>
                            <% } %>
                            <a href="<%= request.getContextPath() %>/cart" class="btn btn-outline-secondary w-100 rounded-pill mt-2 py-2">← Back to Cart</a>
                        </div>
                    </div>
                </div>
            </div>
        </form>

        <div class="mb-5"></div>
    </div>

    <jsp:include page="../footer.jsp" />
</body>
</html>
