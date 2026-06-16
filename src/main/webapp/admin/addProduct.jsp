<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Product</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body class="bg-light">
    <jsp:include page="../navbar.jsp" />

    <div class="container mt-4 flex-grow-1">
        <div class="card shadow-sm mx-auto border-0 rounded-3" style="max-width: 600px;">
            <div class="card-header bg-white border-bottom pb-2 pt-3">
                <h3 class="mb-0 fw-bold">Add New Product</h3>
            </div>
            <div class="card-body p-4 bg-white">
                <% if ("invalid".equals(request.getParameter("error"))) { %>
                    <div class="alert alert-danger p-2 fs-6">Failed to add product. Please check your inputs.</div>
                <% } %>
                <form action="<%= request.getContextPath() %>/addProduct" method="post">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <div class="mb-3">
                        <label class="form-label fw-bold small text-muted">Product Name</label>
                        <input type="text" name="name" class="form-control mb-1 rounded-1" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold small text-muted">Category</label>
                        <select name="category" class="form-select mb-1 rounded-1" required>
                            <option value="Electronics">Electronics</option>
                            <option value="Accessories">Accessories</option>
                            <option value="Clothing">Clothing</option>
                            <option value="Home">Home</option>
                            <option value="Books">Books</option>
                            <option value="Other">Other</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold small text-muted">Price ($)</label>
                        <input type="number" step="0.01" name="price" class="form-control mb-1 rounded-1" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold small text-muted">Image URL</label>
                        <input type="text" name="image" class="form-control mb-1 rounded-1" placeholder="e.g. image.jpg or https://via.placeholder.com/200" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold small text-muted">Initial Stock Quantity</label>
                        <input type="number" name="stock" class="form-control mb-1 rounded-1" value="10" min="0" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold small text-muted">Description</label>
                        <textarea name="description" class="form-control mb-1 rounded-1" rows="4" required></textarea>
                    </div>
                    <div class="pt-2">
                        <button type="submit" class="btn btn-warning w-100 rounded-pill mb-2 shadow-sm py-2 fw-bold" style="background-color: #ffd814;">Save Product</button>
                        <a href="<%= request.getContextPath() %>/adminDashboard" class="btn btn-light border w-100 rounded-pill py-2 text-dark shadow-sm">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <jsp:include page="../footer.jsp" />
</body>
</html>
