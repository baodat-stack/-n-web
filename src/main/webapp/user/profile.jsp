<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.ecommerce.model.User" %>
<%@ page import="com.ecommerce.util.HtmlEscape" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Profile - ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="../navbar.jsp" />

    <div class="container mt-5 flex-grow-1" style="max-width: 800px;">
        <h2 class="mb-4 fw-normal">Your Account</h2>

        <%
            String msg = request.getParameter("msg");
            String error = request.getParameter("error");
            User profileUser = (User) request.getAttribute("profileUser");
        %>

        <% if ("nameUpdated".equals(msg)) { %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                Name updated successfully!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } else if ("passwordChanged".equals(msg)) { %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                Password changed successfully!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } %>

        <% if ("wrongPassword".equals(error)) { %>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                Current password is incorrect.
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } else if ("mismatch".equals(error)) { %>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                New passwords do not match.
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } else if ("shortPassword".equals(error)) { %>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                Password must be at least 6 characters.
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } else if ("emptyName".equals(error)) { %>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                Name cannot be empty.
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } else if ("updateFailed".equals(error)) { %>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                Update failed. Please try again.
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } %>

        <% if (profileUser != null) { %>
        <div class="row g-4">
            <!-- Profile Info Card -->
            <div class="col-md-6">
                <div class="card shadow-sm border-0 h-100">
                    <div class="card-header bg-white border-bottom pt-3 pb-2">
                        <h5 class="mb-0 fw-bold">👤 Profile Information</h5>
                    </div>
                    <div class="card-body p-4">
                        <form action="<%= request.getContextPath() %>/updateProfile" method="post">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="action" value="updateName">
                            <div class="mb-3">
                                <label class="form-label fw-bold small text-muted">Email</label>
                                <input type="email" class="form-control rounded-1 bg-light" value="<%= profileUser.getEmail() %>" disabled>
                                <div class="form-text" style="font-size: 11px;">Email cannot be changed.</div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-bold small text-muted">Full Name</label>
                                <input type="text" name="name" class="form-control rounded-1" value="<%= HtmlEscape.escape(profileUser.getName()) %>" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-bold small text-muted">Role</label>
                                <input type="text" class="form-control rounded-1 bg-light" value="<%= profileUser.getRole() %>" disabled>
                            </div>
                            <button type="submit" class="btn btn-warning w-100 rounded-pill shadow-sm py-2 fw-bold" style="background-color: #ffd814;">Save Changes</button>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Change Password Card -->
            <div class="col-md-6">
                <div class="card shadow-sm border-0 h-100">
                    <div class="card-header bg-white border-bottom pt-3 pb-2">
                        <h5 class="mb-0 fw-bold">🔒 Change Password</h5>
                    </div>
                    <div class="card-body p-4">
                        <form action="<%= request.getContextPath() %>/updateProfile" method="post">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="action" value="changePassword">
                            <div class="mb-3">
                                <label class="form-label fw-bold small text-muted">Current Password</label>
                                <input type="password" name="currentPassword" class="form-control rounded-1" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-bold small text-muted">New Password</label>
                                <input type="password" name="newPassword" class="form-control rounded-1" placeholder="At least 6 characters" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-bold small text-muted">Confirm New Password</label>
                                <input type="password" name="confirmPassword" class="form-control rounded-1" required>
                            </div>
                            <button type="submit" class="btn btn-outline-dark w-100 rounded-pill shadow-sm py-2 fw-bold">Update Password</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <!-- Quick Links -->
        <div class="card shadow-sm border-0 mt-4 mb-5">
            <div class="card-body p-4">
                <h5 class="fw-bold mb-3">Quick Links</h5>
                <div class="d-flex gap-3 flex-wrap">
                    <a href="<%= request.getContextPath() %>/userOrders" class="btn btn-outline-secondary rounded-pill px-4">📦 Your Orders</a>
                    <a href="<%= request.getContextPath() %>/addresses" class="btn btn-outline-secondary rounded-pill px-4">📍 Your Addresses</a>
                    <a href="<%= request.getContextPath() %>/cart" class="btn btn-outline-secondary rounded-pill px-4">🛒 Your Cart</a>
                </div>
            </div>
        </div>
        <% } %>
    </div>

    <jsp:include page="../footer.jsp" />
</body>
</html>
