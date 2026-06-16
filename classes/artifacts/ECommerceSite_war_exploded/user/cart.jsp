<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.ecommerce.model.Cart" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Shopping Cart</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="../navbar.jsp" />

    <div class="container mt-4 flex-grow-1">
        <div class="row">
            <div class="col-lg-9 col-md-8">
                <div class="bg-white p-4 rounded shadow-sm border border-light">
                    <h2 class="mb-4 fw-normal border-bottom pb-2">Shopping Cart</h2>
                    <% 
                        String error = request.getParameter("error");
                        if ("empty".equals(error)) {
                    %>
                        <div class="alert alert-warning">Your cart is empty. Please add items to checkout.</div>
                    <% } else if ("checkoutFailed".equals(error)) { %>
                        <div class="alert alert-danger">Checkout failed. Please try again.</div>
                    <% } %>
                    
                    <% 
                        List<Cart> cartList = (List<Cart>) request.getAttribute("cartList");
                        if (cartList != null && !cartList.isEmpty()) {
                            for (Cart c : cartList) {
                                if (c.getProduct() != null) {
                    %>
                    <div class="row mb-4 border-bottom pb-4 align-items-center">
                        <div class="col-md-3 text-center">
                            <img src="<%= c.getProduct().getImage() %>" class="img-fluid" alt="<%= c.getProduct().getName() %>" style="max-height: 150px; object-fit: contain;">
                        </div>
                        <div class="col-md-7">
                            <h5 class="mb-1"><a href="<%= request.getContextPath() %>/productDetails?id=<%= c.getProduct().getId() %>" class="text-dark text-decoration-none fs-4"><%= c.getProduct().getName() %></a></h5>
                            <p class="text-success small fw-bold mb-0">In Stock</p>
                            <p class="text-muted small mb-2">Category: <%= c.getProduct().getCategory() %></p>
                            
                            <div class="d-flex align-items-center mt-3">
                                <form action="<%= request.getContextPath() %>/updateCart" method="post" class="d-flex align-items-center me-4">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                    <input type="hidden" name="cartId" value="<%= c.getId() %>">
                                    <select name="quantity" class="form-select form-select-sm border border-secondary shadow-sm rounded-2 me-2" style="width: 70px; background-color: #f0f2f2;" onchange="this.form.submit()">
                                        <% for(int i=1; i<=10; i++) { %>
                                            <option value="<%= i %>" <%= (i == c.getQuantity()) ? "selected" : "" %>><%= i %></option>
                                        <% } %>
                                        <option value="0">0 (Delete)</option>
                                    </select>
                                </form>
                                <div class="border-start ps-3">
                                    <a href="<%= request.getContextPath() %>/removeCart?cartId=<%= c.getId() %>" class="text-decoration-none text-info small">Delete</a>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-2 text-end h-100 d-flex flex-column justify-content-start pt-2">
                            <h4 class="font-weight-bold text-dark mb-0">$<%= String.format("%.2f", c.getProduct().getPrice() * c.getQuantity()) %></h4>
                            <% if (c.getQuantity() > 1) { %>
                                <span class="text-muted small">($<%= String.format("%.2f", c.getProduct().getPrice()) %> each)</span>
                            <% } %>
                        </div>
                    </div>
                    <% 
                                }
                            }
                        } else {
                    %>
                    <div class="text-center py-5">
                        <h3 class="text-muted fw-normal">Your ShopSphere Cart is empty.</h3>
                        <a href="<%= request.getContextPath() %>/products" class="btn btn-warning mt-3 rounded-1 shadow-sm px-4">Shop today's deals</a>
                    </div>
                    <% } %>
                    
                    <% if (cartList != null && !cartList.isEmpty()) { 
                        Double totalObj = (Double) request.getAttribute("totalAmount");
                        double total = (totalObj != null) ? totalObj : 0.0;
                    %>
                        <div class="text-end mt-2">
                            <h4 class="fw-normal">Subtotal (<%= cartList.size() %> items): <span class="fw-bold">$<%= String.format("%.2f", total) %></span></h4>
                        </div>
                    <% } %>
                </div>
            </div>
            
            <div class="col-lg-3 col-md-4 mt-4 mt-md-0">
                <div class="bg-white p-4 rounded shadow-sm border border-light text-center">
                    <% 
                        Double totalObj = (Double) request.getAttribute("totalAmount");
                        double total = (totalObj != null) ? totalObj : 0.0;
                        int itemsCount = (cartList != null) ? cartList.size() : 0;
                    %>
                    <h5 class="fw-normal mb-3">Subtotal (<%= itemsCount %> items): <br><span class="fw-bold fs-4">$<%= String.format("%.2f", total) %></span></h5>
                    
                    <% if (cartList != null && !cartList.isEmpty()) { %>
                        <a href="<%= request.getContextPath() %>/checkout" class="btn btn-warning w-100 rounded-pill mt-1 shadow-sm py-2">Proceed to checkout</a>
                    <% } else { %>
                        <button class="btn btn-secondary w-100 rounded-pill mt-1 py-2 opacity-50" disabled>Proceed to checkout</button>
                    <% } %>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="../footer.jsp" />
</body>
</html>
