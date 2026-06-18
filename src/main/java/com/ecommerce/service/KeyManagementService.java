package com.ecommerce.service;

import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import com.ecommerce.dao.AuditLogDAO;
import com.ecommerce.dao.UserKeyDAO;
import com.ecommerce.model.UserKey;
import com.ecommerce.util.AuditAction;

public class KeyManagementService {
    private UserKeyDAO userKeyDAO = new UserKeyDAO();
    private AuditLogDAO auditLogDAO = new AuditLogDAO();

    public int uploadPublicKey(int userId, String publicKeyBase64) throws Exception {
        // 1. Validate format
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyBase64.trim()));
            keyFactory.generatePublic(pubSpec);
        } catch (Exception e) {
            throw new Exception("Invalid RSA 2048 public key format.");
        }

        // 2. Check active key
        if (userKeyDAO.hasActiveKey(userId)) {
            throw new Exception("Bạn đang có khóa hoạt động. Vui lòng báo mất khóa trước.");
        }

        // 3. Compute fingerprint
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(publicKeyBase64.trim().getBytes("UTF-8"));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        String fingerprint = hexString.toString();

        // 4. Save
        int keyId = userKeyDAO.saveKey(userId, publicKeyBase64.trim(), fingerprint);
        if (keyId != -1) {
            auditLogDAO.log(userId, 0, AuditAction.UPLOAD_PUBLIC_KEY, "Uploaded new public key: " + fingerprint);
        } else {
            throw new Exception("Failed to save key to database.");
        }
        
        return keyId;
    }

    public boolean revokeUserKey(int userId) throws Exception {
        UserKey activeKey = userKeyDAO.getActiveKey(userId);
        if (activeKey == null) {
            throw new Exception("No active key found to revoke.");
        }

        boolean success = userKeyDAO.revokeKey(activeKey.getId());
        if (success) {
            auditLogDAO.log(userId, 0, AuditAction.REVOKE_KEY, "Revoked key ID: " + activeKey.getId());
        }
        return success;
    }
}
