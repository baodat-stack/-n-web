<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.ecommerce.model.Address" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Your Addresses - ShopSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="../navbar.jsp" />

    <div class="container mt-5 flex-grow-1" style="max-width: 900px;">
        <h2 class="mb-4 fw-normal">Your Addresses</h2>

        <% String msg = request.getParameter("msg"); %>
        <% String error = request.getParameter("error"); %>

        <% if ("added".equals(msg)) { %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                Address added successfully!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } else if ("deleted".equals(msg)) { %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                Address deleted.
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } %>
        <% if ("emptyFields".equals(error)) { %>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                All fields are required.
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } %>

        <div class="row g-4">
            <!-- Existing Addresses -->
            <%
                List<Address> addresses = (List<Address>) request.getAttribute("addressList");
                if (addresses != null && !addresses.isEmpty()) {
                    for (Address addr : addresses) {
            %>
            <div class="col-md-6">
                <div class="card shadow-sm border-0 h-100">
                    <div class="card-body p-4">
                        <% if (addr.isDefault()) { %>
                            <span class="badge bg-warning text-dark mb-2">Default</span>
                        <% } %>
                        <h5 class="fw-bold mb-1"><%= addr.getFullName() %></h5>
                        <p class="mb-1 text-dark"><%= addr.getAddressLine() %></p>
                        <p class="mb-1 text-dark"><%= addr.getCity() %> - <%= addr.getPincode() %></p>
                        <p class="mb-2 text-muted">Phone: <%= addr.getPhone() %></p>
                        <div class="d-flex gap-2 mt-3">
                            <% if (!addr.isDefault()) { %>
                                <a href="<%= request.getContextPath() %>/setDefaultAddress?id=<%= addr.getId() %>" class="btn btn-sm btn-outline-primary rounded-pill px-3">Set as Default</a>
                            <% } %>
                            <a href="<%= request.getContextPath() %>/deleteAddress?id=<%= addr.getId() %>" class="btn btn-sm btn-outline-danger rounded-pill px-3" onclick="return confirm('Delete this address?');">Delete</a>
                        </div>
                    </div>
                </div>
            </div>
            <%
                    }
                }
            %>

            <!-- Add New Address Card -->
            <div class="col-md-6">
                <div class="card shadow-sm border-0 h-100 border-dashed">
                    <div class="card-body p-4">
                        <h5 class="fw-bold mb-3">➕ Add New Address</h5>
                        <form action="<%= request.getContextPath() %>/addresses" method="post">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <div class="mb-2">
                                <input type="text" name="fullName" class="form-control form-control-sm rounded-1" placeholder="Full Name" required>
                            </div>
                            <div class="mb-2">
                                <input type="text" name="phone" class="form-control form-control-sm rounded-1" placeholder="Phone Number" required>
                            </div>
                            <div class="mb-2">
                                <input type="text" name="addressLine" class="form-control form-control-sm rounded-1" placeholder="Address Line" required>
                            </div>
                            <div class="row g-2 mb-2">
                                <div class="col">
                                    <input type="text" name="city" class="form-control form-control-sm rounded-1" placeholder="City" required>
                                </div>
                                <div class="col">
                                    <input type="text" name="pincode" class="form-control form-control-sm rounded-1" placeholder="Pincode" required>
                                </div>
                            </div>
                            <button type="submit" class="btn btn-warning w-100 rounded-pill shadow-sm py-2 mt-2 fw-bold" style="background-color: #ffd814;">Save Address</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <div class="mt-4 mb-5">
            <a href="<%= request.getContextPath() %>/profile" class="btn btn-outline-secondary rounded-pill px-4">← Back to Profile</a>
        </div>
    </div>

    <jsp:include page="../footer.jsp" />
</body>
</html>
