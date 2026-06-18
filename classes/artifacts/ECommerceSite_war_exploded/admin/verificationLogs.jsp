<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.ecommerce.model.VerificationLog" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Verification Logs - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body class="bg-light">
    <jsp:include page="../navbar.jsp" />

    <div class="container mt-4 flex-grow-1">
        <div class="d-flex justify-content-between align-items-center mb-4 bg-white p-3 rounded shadow-sm border">
            <h2 class="mb-0 fw-bold text-dark">Signature Verification Logs</h2>
            <a href="<%= request.getContextPath() %>/adminDashboard" class="btn btn-outline-dark fw-bold px-4">Back to Dashboard</a>
        </div>
        
        <div class="table-responsive">
            <table class="table table-bordered table-custom shadow-sm bg-white table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th width="15%">Time</th>
                        <th width="10%">Order ID</th>
                        <th width="10%">User ID</th>
                        <th width="15%">Result</th>
                        <th width="50%">Message</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        List<VerificationLog> logs = (List<VerificationLog>) request.getAttribute("verifyLogs");
                        if (logs != null && !logs.isEmpty()) {
                            for (VerificationLog l : logs) {
                                String badgeClass = "secondary";
                                if ("VERIFIED".equals(l.getResult())) badgeClass = "success";
                                else if ("INVALID".equals(l.getResult())) badgeClass = "danger";
                                else if ("TAMPERED".equals(l.getResult())) badgeClass = "warning text-dark";
                                else if ("KEY_REVOKED".equals(l.getResult())) badgeClass = "dark";
                    %>
                    <tr>
                        <td class="small text-muted"><%= l.getVerifyTime() %></td>
                        <td class="fw-bold">#<%= l.getOrderId() %></td>
                        <td class="fw-bold"><%= l.getUserId() %></td>
                        <td><span class="badge bg-<%= badgeClass %> px-2 py-1"><%= l.getResult() %></span></td>
                        <td class="small"><%= l.getMessage() %></td>
                    </tr>
                    <%      }
                        } else {
                    %>
                    <tr>
                        <td colspan="5" class="text-center py-5 text-muted">No verification logs found.</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>

    <jsp:include page="../footer.jsp" />
</body>
</html>
