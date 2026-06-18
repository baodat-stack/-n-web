<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.ecommerce.model.AuditLog" %>
<%@ page import="com.ecommerce.util.AuditAction" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Audit Logs - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body class="bg-light">
    <jsp:include page="../navbar.jsp" />

    <div class="container mt-4 flex-grow-1">
        <div class="d-flex justify-content-between align-items-center mb-4 bg-white p-3 rounded shadow-sm border">
            <h2 class="mb-0 fw-bold text-dark">System Audit Logs</h2>
            <a href="<%= request.getContextPath() %>/adminDashboard" class="btn btn-outline-dark fw-bold px-4">Back to Dashboard</a>
        </div>
        
        <div class="card mb-4 shadow-sm border-0">
            <div class="card-body p-3 bg-white rounded">
                <form action="<%= request.getContextPath() %>/admin/auditLogs" method="get" class="d-flex align-items-center gap-3">
                    <label class="fw-bold mb-0">Filter by Action:</label>
                    <% String currentFilter = (String) request.getAttribute("currentFilter"); %>
                    <select name="actionType" class="form-select w-auto">
                        <option value="">-- All Actions --</option>
                        <% for (AuditAction a : AuditAction.values()) { %>
                            <option value="<%= a.name() %>" <%= a.name().equals(currentFilter) ? "selected" : "" %>><%= a.name() %></option>
                        <% } %>
                    </select>
                    <button type="submit" class="btn btn-primary px-4">Filter</button>
                    <a href="<%= request.getContextPath() %>/admin/auditLogs" class="btn btn-outline-secondary">Clear</a>
                </form>
            </div>
        </div>

        <div class="table-responsive">
            <table class="table table-bordered table-custom shadow-sm bg-white table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th width="15%">Time</th>
                        <th width="10%">User ID</th>
                        <th width="10%">Order ID</th>
                        <th width="15%">Action Type</th>
                        <th width="50%">Description</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        List<AuditLog> logs = (List<AuditLog>) request.getAttribute("auditLogs");
                        if (logs != null && !logs.isEmpty()) {
                            for (AuditLog l : logs) {
                    %>
                    <tr>
                        <td class="small text-muted"><%= l.getCreatedAt() %></td>
                        <td class="fw-bold"><%= l.getUserId() > 0 ? l.getUserId() : "System" %></td>
                        <td><%= l.getOrderId() > 0 ? "#" + l.getOrderId() : "-" %></td>
                        <td><span class="badge bg-secondary"><%= l.getActionType() %></span></td>
                        <td class="small"><%= l.getDescription() %></td>
                    </tr>
                    <%      }
                        } else {
                    %>
                    <tr>
                        <td colspan="5" class="text-center py-5 text-muted">No audit logs found.</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>

    <jsp:include page="../footer.jsp" />
</body>
</html>
