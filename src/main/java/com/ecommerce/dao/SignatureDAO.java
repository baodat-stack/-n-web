package com.ecommerce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.ecommerce.model.OrderSignature;
import com.ecommerce.util.DBConnection;

public class SignatureDAO {

    public boolean hasSignature(int orderId) {
        String sql = "SELECT COUNT(*) FROM order_signatures WHERE order_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean saveSignature(int orderId, int keyId, int requestId, String sigData, String verifyStatus) {
        String sql = "INSERT INTO order_signatures (order_id, key_id, request_id, signature_data, verify_status) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, keyId);
            ps.setInt(3, requestId);
            ps.setString(4, sigData);
            ps.setString(5, verifyStatus);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public OrderSignature getSignatureByOrder(int orderId) {
        String sql = "SELECT * FROM order_signatures WHERE order_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    OrderSignature sig = new OrderSignature();
                    sig.setId(rs.getInt("id"));
                    sig.setOrderId(rs.getInt("order_id"));
                    sig.setKeyId(rs.getInt("key_id"));
                    sig.setRequestId(rs.getInt("request_id"));
                    sig.setSignatureData(rs.getString("signature_data"));
                    sig.setSignTime(rs.getString("sign_time"));
                    sig.setVerifyStatus(rs.getString("verify_status"));
                    return sig;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
