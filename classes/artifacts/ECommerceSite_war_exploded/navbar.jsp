<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.ecommerce.model.User" %>
<% User currentUser = (User) session.getAttribute("currentUser"); %>
<nav class="navbar navbar-expand-lg navbar-dark" style="background-color: #131921;">
  <div class="container">
    <a class="navbar-brand" href="<%= request.getContextPath() %>/products">SportSphere</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNav">
      <form class="d-flex mx-auto w-50" action="<%= request.getContextPath() %>/search" method="get">
        <input class="form-control me-2 rounded-1" type="search" name="q" placeholder="Search products..." aria-label="Search" required>
        <button class="btn btn-warning rounded-1 px-4 text-dark" style="background-color:#febd69; border:none;" type="submit">🔍</button>
      </form>
      <ul class="navbar-nav ms-auto align-items-center">
        <li class="nav-item">
          <a class="nav-link text-white" href="<%= request.getContextPath() %>/products">Home</a>
        </li>
        <% if (currentUser != null && "admin".equals(currentUser.getRole())) { %>
            <li class="nav-item">
                <a class="nav-link text-warning fw-bold" href="<%= request.getContextPath() %>/adminDashboard">Admin</a>
            </li>
            <li class="nav-item">
                <a class="nav-link text-white" href="<%= request.getContextPath() %>/admin/auditLogs">Audit Logs</a>
            </li>
            <li class="nav-item">
                <a class="nav-link text-white" href="<%= request.getContextPath() %>/admin/verificationLogs">Verify Logs</a>
            </li>
        <% } %>
        <% if (currentUser != null) { %>
            <li class="nav-item">
                <a class="nav-link text-white" href="<%= request.getContextPath() %>/profile">Profile</a>
            </li>
            <li class="nav-item">
                <a class="nav-link text-white" href="<%= request.getContextPath() %>/userOrders">Orders</a>
            </li>
            <li class="nav-item">
                <a class="nav-link text-info" href="<%= request.getContextPath() %>/user/uploadPublicKey">Key Management</a>
            </li>
            <li class="nav-item">
                <a class="nav-link text-white d-flex align-items-center" href="<%= request.getContextPath() %>/cart">
                    <span class="me-1">🛒</span> Cart
                </a>
            </li>
            <li class="nav-item ms-2">
                <a class="btn btn-outline-light btn-sm" href="<%= request.getContextPath() %>/logout">Logout</a>
            </li>
        <% } else { %>
            <li class="nav-item">
                <a class="nav-link text-white" href="<%= request.getContextPath() %>/user/login.jsp">Login</a>
            </li>
            <li class="nav-item ms-1">
                <a class="btn btn-warning btn-sm text-dark fw-bold" href="<%= request.getContextPath() %>/user/register.jsp">Sign Up</a>
            </li>
        <% } %>
      </ul>
    </div>
  </div>
</nav>
