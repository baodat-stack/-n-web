<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>ShopSphere - Login</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
    <div class="container d-flex justify-content-center align-items-center" style="min-height: 100vh;">
        <div class="card p-4 shadow-sm" style="width: 100%; max-width: 400px; border-radius: 8px; border: 1px solid #ddd;">
            <div class="text-center mb-4">
                <a href="<%= request.getContextPath() %>/products" class="h2 text-dark text-decoration-none fw-bold" style="color: #111 !important;">ShopSphere</a>
            </div>
            <h3 class="mb-3 fw-normal" style="font-size: 28px;">Sign in</h3>
            
            <% if ("invalid".equals(request.getParameter("error"))) { %>
                <div class="alert alert-danger p-3 fs-6 rounded-1">
                    <i class="fw-bold text-danger">!</i> Invalid email or password.
                </div>
            <% } %>
            <% if ("registered".equals(request.getParameter("msg"))) { %>
                <div class="alert alert-success p-3 fs-6 rounded-1">Registration successful! Please login.</div>
            <% } %>
            <% if ("logout".equals(request.getParameter("msg"))) { %>
                <div class="alert alert-info p-3 fs-6 rounded-1">You have been logged out.</div>
            <% } %>

            <form action="<%= request.getContextPath() %>/login" method="post">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <div class="mb-3">
                    <label class="form-label fw-bold" style="font-size: 13px;">Email</label>
                    <input type="email" name="email" class="form-control rounded-1" required>
                </div>
                <div class="mb-4">
                    <label class="form-label fw-bold" style="font-size: 13px;">Password</label>
                    <input type="password" name="password" class="form-control rounded-1" required>
                </div>
                <button type="submit" class="btn w-100 rounded-pill mb-3 shadow-sm border border-dark" style="background-color: #ffd814;">Continue</button>
            </form>
            
            <p class="small text-muted mt-2" style="font-size: 12px;">By continuing, you agree to ShopSphere's Conditions of Use and Privacy Notice.</p>
            
            <div class="text-center mt-3 pt-3 border-top position-relative">
                <div class="position-absolute top-0 start-50 translate-middle bg-white px-2 small text-muted" style="margin-top: -1px;">New to ShopSphere?</div>
                <a href="<%= request.getContextPath() %>/user/register.jsp" class="btn btn-light border w-100 mt-2 shadow-sm rounded-1 btn-sm py-2 bg-gradient">Create your ShopSphere account</a>
            </div>


        </div>
    </div>
</body>
</html>
