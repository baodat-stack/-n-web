<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.ecommerce.model.Product" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Product Details</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="navbar.jsp" />

    <div class="container mt-5 flex-grow-1">
        <% 
            Product p = (Product) request.getAttribute("product");
            if (p != null) {
        %>
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="<%= request.getContextPath() %>/products">Home</a></li>
                <li class="breadcrumb-item"><a href="<%= request.getContextPath() %>/search?q=<%= p.getCategory() %>"><%= p.getCategory() %></a></li>
                <li class="breadcrumb-item active" aria-current="page"><%= p.getName() %></li>
            </ol>
        </nav>
        
        <div class="row mt-4">
            <div class="col-md-5 text-center">
                <% String imgSrc = (p.getImage() != null && !p.getImage().isEmpty()) ? p.getImage() : "https://via.placeholder.com/400"; %>
                <img src="<%= imgSrc %>" class="img-fluid border p-3 bg-white" alt="<%= p.getName() %>" style="max-height: 400px;">
            </div>
            <div class="col-md-7 px-4">
                <h2 class="fw-bold"><%= p.getName() %></h2>
                <hr>
                <h3 class="text-danger my-3">$<%= String.format("%.2f", p.getPrice()) %></h3>
                <h6 class="fw-bold mt-4">About this item</h6>
                <p class="text-dark"><%= p.getDescription() %></p>
                
                <div class="mt-5 p-4 border rounded bg-white shadow-sm" style="max-width: 300px;">
                    <% if (p.getStock() > 0 && p.getStock() <= 5) { %>
                        <h5 class="text-warning mb-2 fw-bold">Only <%= p.getStock() %> left!</h5>
                    <% } else if (p.getStock() <= 0) { %>
                        <h5 class="text-danger mb-2 fw-bold">Out of Stock</h5>
                    <% } else { %>
                        <h5 class="text-success mb-2 fw-bold">In Stock.</h5>
                    <% } %>

                    <% if (p.getStock() > 0) { %>
                        <a href="<%= request.getContextPath() %>/addToCart?productId=<%= p.getId() %>" class="btn btn-warning w-100 rounded-pill mt-2 mb-2 shadow-sm">Add to Cart</a>
                    <% } else { %>
                        <button class="btn btn-secondary w-100 rounded-pill mt-2 mb-2 shadow-sm" disabled>Out of Stock</button>
                    <% } %>
                    <a href="<%= request.getContextPath() %>/products" class="btn btn-outline-secondary w-100 rounded-pill">Back to Shop</a>
                </div>
            </div>
        </div>
        <% } else { %>
            <div class="alert alert-danger mt-4">Product not found.</div>
        <% } %>
    </div>

    <jsp:include page="footer.jsp" />
</body>
</html>
