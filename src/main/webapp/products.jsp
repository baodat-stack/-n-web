<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.ecommerce.model.Product" %>
<%@ page import="com.ecommerce.util.HtmlEscape" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>ShopSphere - Products</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="navbar.jsp" />

    <div class="container mt-4 flex-grow-1">
        <% 
            String selectedCategory = (String) request.getAttribute("selectedCategory");
            String searchQuery = (String) request.getAttribute("searchQuery");
            if (searchQuery != null) {
        %>
            <h4 class="mb-4">Search Results for: "<%= HtmlEscape.escape(searchQuery) %>"</h4>
        <% } else { %>
            <div class="p-4 mb-4 bg-white rounded-3 shadow-sm border text-center" style="background: linear-gradient(180deg, #f8f9fa 0%, #e9ecef 100%);">
                <h1 class="display-5 fw-bold text-dark">Welcome to ShopSphere</h1>
                <p class="col-md-8 mx-auto fs-5 text-muted">Your simple destination for amazing products.</p>
            </div>

            <!-- Category Filter Pills -->
            <c:if test="${not empty categories}">
                <div class="d-flex flex-wrap gap-2 mb-4 align-items-center">
                    <span class="text-muted small fw-bold me-2">Filter:</span>
                    <a href="<%= request.getContextPath() %>/products" 
                       class="btn btn-sm rounded-pill px-3 shadow-sm ${empty selectedCategory or selectedCategory == 'all' ? 'btn-dark' : 'btn-outline-dark'}">All</a>
                    
                    <c:forEach var="cat" items="${categories}">
                        <a href="<%= request.getContextPath() %>/products?category=${cat}" 
                           class="btn btn-sm rounded-pill px-3 shadow-sm ${cat == selectedCategory ? 'btn-dark' : 'btn-outline-dark'}">${cat}</a>
                    </c:forEach>
                </div>
            </c:if>

            <h3 class="mb-4 text-start">
                <c:choose>
                    <c:when test="${not empty selectedCategory and selectedCategory != 'all'}">
                        <c:out value="${selectedCategory}" />
                    </c:when>
                    <c:otherwise>
                        Featured Products
                    </c:otherwise>
                </c:choose>
            </h3>
        <% } %>

        <% 
            String error = request.getParameter("error");
            if ("CartFailed".equals(error)) {
        %>
            <div class="alert alert-danger">Failed to add item to cart. Please log in first.</div>
        <% } %>

        <div class="row row-cols-1 row-cols-md-4 g-4 mb-4">
            <% 
                List<Product> products = (List<Product>) request.getAttribute("productList");
                if (products != null && !products.isEmpty()) {
                    for (Product p : products) {
            %>
            <div class="col">
                <div class="card h-100 p-2">
                    <% String imgSrc = (p.getImage() != null && !p.getImage().isEmpty()) ? p.getImage() : "https://via.placeholder.com/200"; %>
                    <img src="<%= imgSrc %>" class="card-img-top product-img" alt="<%= p.getName() %>">
                    <div class="card-body d-flex flex-column pt-1">
                        <h5 class="card-title text-truncate mb-1" title="<%= p.getName() %>"><%= p.getName() %></h5>
                        <p class="card-text text-muted small mb-1"><%= p.getCategory() %></p>
                        <h4 class="card-text text-dark mb-2">$<%= String.format("%.2f", p.getPrice()) %></h4>
                        <% if (p.getStock() > 0 && p.getStock() <= 5) { %>
                            <p class="text-warning small fw-bold mb-2">Only <%= p.getStock() %> left in stock!</p>
                        <% } else if (p.getStock() <= 0) { %>
                            <p class="text-danger small fw-bold mb-2">Out of Stock</p>
                        <% } else { %>
                            <p class="text-success small fw-bold mb-2">In Stock</p>
                        <% } %>
                        <div class="mt-auto">
                            <% if (p.getStock() > 0) { %>
                                <a href="<%= request.getContextPath() %>/addToCart?productId=<%= p.getId() %>" class="btn btn-warning w-100 rounded-pill mb-2 shadow-sm">Add to Cart</a>
                            <% } else { %>
                                <button class="btn btn-secondary w-100 rounded-pill mb-2 shadow-sm" disabled>Out of Stock</button>
                            <% } %>
                            <a href="<%= request.getContextPath() %>/productDetails?id=<%= p.getId() %>" class="btn btn-outline-secondary btn-sm w-100 rounded-pill">View Details</a>
                        </div>
                    </div>
                </div>
            </div>
            <% 
                    }
                } else {
            %>
            <div class="col-12 text-center mt-5">
                <h4 class="text-muted">No products found.</h4>
            </div>
            <% } %>
        </div>

        <!-- Pagination Controls -->
        <c:if test="${totalPages > 1}">
            <c:set var="categoryParam" value="${not empty selectedCategory and selectedCategory != 'all' ? '&category='.concat(selectedCategory) : ''}" />
            <nav aria-label="Product pagination" class="mb-5">
                <ul class="pagination justify-content-center">
                    <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                        <a class="page-link" href="<%= request.getContextPath() %>/products?page=${currentPage - 1}${categoryParam}">&laquo; Previous</a>
                    </li>
                    <c:forEach var="i" begin="1" end="${totalPages}">
                        <li class="page-item ${i == currentPage ? 'active' : ''}">
                            <a class="page-link" href="<%= request.getContextPath() %>/products?page=${i}${categoryParam}">${i}</a>
                        </li>
                    </c:forEach>
                    <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                        <a class="page-link" href="<%= request.getContextPath() %>/products?page=${currentPage + 1}${categoryParam}">Next &raquo;</a>
                    </li>
                </ul>
            </nav>
        </c:if>
        <c:if test="${totalPages <= 1}">
            <div class="mb-5"></div>
        </c:if>
    </div>

    <jsp:include page="footer.jsp" />
</body>
</html>
