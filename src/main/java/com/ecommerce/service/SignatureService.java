package com.ecommerce.service;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import com.ecommerce.dao.AuditLogDAO;
import com.ecommerce.dao.OrderDAO;
import com.ecommerce.dao.OrderSignRequestDAO;
import com.ecommerce.dao.SignatureDAO;
import com.ecommerce.dao.UserKeyDAO;
import com.ecommerce.dao.VerificationLogDAO;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderSignRequest;
import com.ecommerce.model.UserKey;
import com.ecommerce.util.AuditAction;
import com.ecommerce.util.OrderJsonBuilder;

public class SignatureService {
    private SignatureDAO signatureDAO = new SignatureDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private UserKeyDAO userKeyDAO = new UserKeyDAO();
    private OrderSignRequestDAO signRequestDAO = new OrderSignRequestDAO();
    private VerificationLogDAO verifyLogDAO = new VerificationLogDAO();
    private AuditLogDAO auditLogDAO = new AuditLogDAO();

    public String verifyAndSaveSignature(int orderId, int requestId, byte[] sigBytes, int userId) throws Exception {
        // Step 0
        if (signatureDAO.hasSignature(orderId)) {
            throw new Exception("Đơn này đã có chữ ký. Không cho phép upload lại.");
        }

        // Step 1
        Order order = orderDAO.getOrderById(orderId);
        if (order == null) throw new Exception("Order not found.");
        if (!"Pending".equalsIgnoreCase(order.getStatus()) && !"Processing".equalsIgnoreCase(order.getStatus())) {
            throw new Exception("Đơn hàng không được phép ký ở trạng thái hiện tại.");
        }

        // Step 2
        UserKey activeKey = userKeyDAO.getActiveKey(userId);
        if (activeKey == null) {
            verifyLogDAO.log(orderId, userId, "KEY_REVOKED", "No active key during signature upload.");
            auditLogDAO.log(userId, orderId, AuditAction.VERIFY, "Verification failed: KEY_REVOKED");
            return "KEY_REVOKED";
        }

        // Step 3
        OrderSignRequest signRequest = signRequestDAO.getById(requestId);
        if (signRequest == null || signRequest.getOrderId() != orderId) {
            throw new Exception("Request ID không hợp lệ.");
        }

        String currentJson = OrderJsonBuilder.buildJson(orderId, requestId);
        String currentHash = OrderJsonBuilder.computeHash(currentJson);
        
        if (!currentHash.equals(signRequest.getOriginalOrderHash())) {
            verifyLogDAO.log(orderId, userId, "TAMPERED", "Order data changed after download.");
            auditLogDAO.log(userId, orderId, AuditAction.VERIFY, "Verification failed: TAMPERED");
            return "TAMPERED";
        }

        // Step 4
        boolean isValid = false;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(Base64.getDecoder().decode(activeKey.getPublicKey().trim()));
            PublicKey publicKey = keyFactory.generatePublic(pubSpec);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(signRequest.getOriginalOrderJson().getBytes("UTF-8"));

            isValid = signature.verify(sigBytes);
        } catch (Exception e) {
            isValid = false;
        }

        if (isValid) {
            String sigData = Base64.getEncoder().encodeToString(sigBytes);
            signatureDAO.saveSignature(orderId, activeKey.getId(), requestId, sigData, "VERIFIED");
            verifyLogDAO.log(orderId, userId, "VERIFIED", "Signature verified successfully.");
            auditLogDAO.log(userId, orderId, AuditAction.UPLOAD_SIG, "Uploaded signature for order");
            auditLogDAO.log(userId, orderId, AuditAction.VERIFY, "Verification success: VERIFIED");
            return "VERIFIED";
        } else {
            verifyLogDAO.log(orderId, userId, "INVALID", "Signature verification failed (wrong key/sig).");
            auditLogDAO.log(userId, orderId, AuditAction.VERIFY, "Verification failed: INVALID");
            return "INVALID";
        }
    }
}
