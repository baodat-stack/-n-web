<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.ecommerce.model.UserKey" %>
<%@ page import="com.ecommerce.model.KeyHistory" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Key Management - SportSphere</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body class="bg-light">
    <jsp:include page="../navbar.jsp" />

    <div class="container mt-5 flex-grow-1" style="max-width: 900px;">
        <h2 class="mb-4 fw-normal">Digital Signature Key Management</h2>
        
        <% 
            String errorMsg = (String) session.getAttribute("errorMsg");
            if (errorMsg != null) { 
        %>
            <div class="alert alert-danger shadow-sm"><%= errorMsg %></div>
        <% 
                session.removeAttribute("errorMsg");
            } 
            String successMsg = request.getParameter("success");
            if (successMsg != null) {
        %>
            <div class="alert alert-success shadow-sm"><%= successMsg %></div>
        <% } %>

        <% 
            UserKey activeKey = (UserKey) request.getAttribute("activeKey");
            if (activeKey != null) { 
        %>
            <div class="card mb-4 border border-success rounded-3 shadow-sm">
                <div class="card-header bg-success text-white py-3">
                    <h5 class="mb-0">✅ Active Key Found</h5>
                </div>
                <div class="card-body p-4">
                    <p class="mb-1"><strong>Status:</strong> <span class="badge bg-success">ACTIVE</span></p>
                    <p class="mb-1"><strong>Issue Date:</strong> <%= activeKey.getIssueDate() %></p>
                    <p class="mb-3"><strong>Fingerprint (SHA-256):</strong> <code class="bg-light p-1 rounded"><%= activeKey.getFingerprint() %></code></p>
                    
                    <div class="alert alert-warning text-dark border-warning">
                        <strong>Lưu ý:</strong> Bạn đang có khóa hoạt động. Vui lòng báo mất khóa trước khi upload khóa mới.
                    </div>
                    
                    <form action="<%= request.getContextPath() %>/user/revokeKey" method="post" onsubmit="return confirm('Bạn có chắc chắn muốn thu hồi khóa này? Mọi đơn hàng ký bằng khóa này sau thời điểm thu hồi sẽ không hợp lệ.');">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                        <button type="submit" class="btn btn-danger rounded-pill px-4">Report Lost Key / Revoke</button>
                    </form>
                </div>
            </div>
        <% } else { %>
            <div class="card mb-4 border rounded-3 shadow-sm">
                <div class="card-header bg-white py-3">
                    <h5 class="mb-0">Upload Public Key</h5>
                </div>
                <div class="card-body p-4">
                    <p class="text-muted">You do not have an active key. Please generate a key pair using the DigitalSignTool and upload your <strong>public.key</strong> file here.</p>
                    <form action="<%= request.getContextPath() %>/user/uploadPublicKey" method="post" enctype="multipart/form-data">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                        <div class="mb-3">
                            <label for="publicKeyFile" class="form-label fw-bold">Select public.key file:</label>
                            <input class="form-control" type="file" id="publicKeyFile" name="publicKeyFile" required accept=".key">
                        </div>
                        <button type="submit" class="btn btn-warning rounded-pill px-4">Upload Key</button>
                    </form>
                </div>
            </div>
        <% } %>

        <div class="card mb-5 border rounded-3 shadow-sm">
            <div class="card-header bg-white py-3">
                <h5 class="mb-0">Key History</h5>
            </div>
            <div class="card-body p-0">
                <table class="table table-hover mb-0">
                    <thead class="table-light">
                        <tr>
                            <th>Date</th>
                            <th>Action</th>
                            <th>Key ID</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                            List<KeyHistory> history = (List<KeyHistory>) request.getAttribute("keyHistory");
                            if (history != null && !history.isEmpty()) {
                                for (KeyHistory h : history) {
                        %>
                            <tr>
                                <td><%= h.getActionDate() %></td>
                                <td>
                                    <% if ("GENERATED".equals(h.getAction())) { %>
                                        <span class="badge bg-success">GENERATED</span>
                                    <% } else { %>
                                        <span class="badge bg-danger">REVOKED</span>
                                    <% } %>
                                </td>
                                <td>#<%= h.getKeyId() %></td>
                            </tr>
                        <% 
                                }
                            } else { 
                        %>
                            <tr>
                                <td colspan="3" class="text-center py-3 text-muted">No history found.</td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>

    </div>

    <jsp:include page="../footer.jsp" />
</body>
</html>
