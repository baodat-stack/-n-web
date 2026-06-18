<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.ecommerce.model.Order" %>
<%@ page import="com.ecommerce.model.OrderSignature" %>
<%@ page import="com.ecommerce.dao.SignatureDAO" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>All Orders</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body class="bg-light">
    <jsp:include page="../navbar.jsp" />

    <div class="container mt-4 flex-grow-1">
        <div class="d-flex justify-content-between align-items-center mb-4 bg-white p-3 rounded shadow-sm border">
            <h2 class="mb-0 fw-bold text-dark">All Customer Orders</h2>
            <a href="<%= request.getContextPath() %>/adminDashboard" class="btn btn-outline-dark fw-bold px-4">Back to Dashboard</a>
        </div>
        
        <div class="table-responsive">
            <table class="table table-bordered table-custom shadow-sm bg-white table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th width="15%">Order ID</th>
                        <th width="15%">User ID</th>
                        <th width="15%">Total Amount</th>
                        <th width="15%">Order Date</th>
                        <th width="10%">Status</th>
                        <th width="15%">Signature</th>
                        <th width="20%">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        List<Order> orderList = (List<Order>) request.getAttribute("orderList");
                        if (orderList != null && !orderList.isEmpty()) {
                            for (Order o : orderList) {
                    %>
                    <tr>
                        <td><strong class="text-info">#<%= o.getId() %></strong></td>
                        <td class="fw-bold"><%= o.getUserId() %></td>
                        <td class="fw-bold fs-6 text-danger">$<%= String.format("%.2f", o.getTotalAmount()) %></td>
                        <td class="small text-muted"><%= o.getOrderDate() %></td>
                        <td>
                            <span class="badge bg-<%= "Delivered".equals(o.getStatus()) ? "success" : "warning text-dark" %> px-3 py-2 rounded-1 shadow-sm"><%= o.getStatus() %></span>
                        </td>
                        <td>
                            <% 
                                SignatureDAO sigDAO = new SignatureDAO();
                                OrderSignature os = sigDAO.getSignatureByOrder(o.getId());
                                if (os != null) {
                                    String badge = "secondary";
                                    if ("VERIFIED".equals(os.getVerifyStatus())) badge = "success";
                                    else if ("INVALID".equals(os.getVerifyStatus())) badge = "danger";
                                    else if ("TAMPERED".equals(os.getVerifyStatus())) badge = "warning text-dark";
                                    else if ("KEY_REVOKED".equals(os.getVerifyStatus())) badge = "dark";
                            %>
                                <span class="badge bg-<%= badge %>"><%= os.getVerifyStatus() %></span>
                            <%  } else { %>
                                <span class="badge bg-light text-secondary border">Unsigned</span>
                            <%  } %>
                        </td>
                        <td>
                            <div class="dropdown w-100">
                                <button class="btn btn-sm text-dark fw-bold shadow-sm rounded-1 px-3 w-100 mb-1 dropdown-toggle" type="button" data-bs-toggle="dropdown" style="background-color: #e3e6e6; border: 1px solid #d5d9d9;">
                                    Update Status
                                </button>
                                <ul class="dropdown-menu shadow-sm border-0">
                                    <li><a class="dropdown-item small" href="<%= request.getContextPath() %>/updateOrderStatus?id=<%= o.getId() %>&status=Shipped">Mark Shipped</a></li>
                                    <li><a class="dropdown-item small" href="<%= request.getContextPath() %>/updateOrderStatus?id=<%= o.getId() %>&status=Delivered">Mark Delivered</a></li>
                                </ul>
                            </div>
                        </td>
                    </tr>
                    <%      }
                        } else {
                    %>
                    <tr>
                        <td colspan="6" class="text-center py-5 text-muted">
                            <h5>No orders found in the system.</h5>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>

    <jsp:include page="../footer.jsp" />
</body>
</html>
