<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>ShopSphere - Register</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
    <div class="container d-flex justify-content-center align-items-center" style="min-height: 100vh;">
        <div class="card p-4 shadow-sm" style="width: 100%; max-width: 400px; border-radius: 8px; border: 1px solid #ddd;">
            <div class="text-center mb-4">
                <a href="<%= request.getContextPath() %>/products" class="h2 text-dark text-decoration-none fw-bold" style="color: #111 !important;">ShopSphere</a>
            </div>
            <h3 class="mb-3 fw-normal" style="font-size: 28px;">Create account</h3>

            <% if ("invalid".equals(request.getParameter("error"))) { %>
                <div class="alert alert-danger p-3 fs-6 rounded-1">
                    <i class="fw-bold text-danger">!</i> Registration failed. Email might already exist.
                </div>
            <% } %>

            <form action="<%= request.getContextPath() %>/register" method="post">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <div class="mb-3">
                    <label class="form-label fw-bold" style="font-size: 13px;">Your name</label>
                    <input type="text" name="name" class="form-control rounded-1" placeholder="First and last name" required>
                </div>
                <div class="mb-3">
                    <label class="form-label fw-bold" style="font-size: 13px;">Email</label>
                    <input type="email" name="email" class="form-control rounded-1" required>
                </div>
                <div class="mb-4">
                    <label class="form-label fw-bold" style="font-size: 13px;">Password</label>
                    <input type="password" name="password" class="form-control rounded-1" placeholder="At least 6 characters" required>
                    <div class="form-text" style="font-size: 11px;">Passwords must be at least 6 characters.</div>
                </div>
                <button type="submit" class="btn w-100 rounded-pill mb-3 shadow-sm border border-dark" style="background-color: #ffd814;">Continue</button>
            </form>
            
            <p class="small text-muted mt-1" style="font-size: 12px;">By creating an account, you agree to ShopSphere's Conditions of Use and Privacy Notice.</p>
            
            <div class="mt-2 pt-3 border-top">
                <p class="small mb-0" style="font-size: 13px;">Already have an account? <a href="<%= request.getContextPath() %>/user/login.jsp" class="text-primary text-decoration-none">Sign in</a></p>
            </div>
        </div>
    </div>
</body>
</html>
