<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Order Placed Successfully</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="../navbar.jsp" />

    <div class="container mt-5 flex-grow-1 text-center">
        <div class="card p-5 shadow-sm border-0 bg-white mx-auto rounded-4" style="max-width: 600px;">
            <div class="text-success mb-2" style="font-size: 5rem; line-height: 1;">✓</div>
            <h2 class="text-success fw-bold mb-3">Order Placed, thank you!</h2>
            <p class="fs-5 text-dark mb-4">Confirmation will be sent to your email.</p>
            <div class="text-start bg-light p-4 rounded-3 border mb-4">
                <p class="mb-1 text-muted small text-uppercase fw-bold">Payment Status: <span class="badge bg-success">Success (Simulated)</span></p>
                <p class="mb-1 text-muted small text-uppercase fw-bold mt-3">Shipping to:</p>
                <p class="mb-0 text-dark">Your selected address.</p>
            </div>
            <div class="d-flex justify-content-center gap-3">
                <a href="<%= request.getContextPath() %>/userOrders" class="btn btn-warning px-4 rounded-pill shadow-sm py-2 px-4 shadow-sm border border-warning">Review or edit recent orders</a>
                <a href="<%= request.getContextPath() %>/products" class="btn btn-outline-secondary px-4 rounded-pill shadow-sm py-2 px-4">Continue Shopping</a>
            </div>
        </div>
    </div>

    <jsp:include page="../footer.jsp" />
</body>
</html>
