package com.ecommerce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.ecommerce.model.OrderSignRequest;
import com.ecommerce.util.DBConnection;

public class OrderSignRequestDAO {

    public int save(int orderId, int userId, String originalJson, String originalHash) {
        String sql = "INSERT INTO order_sign_requests (order_id, user_id, original_order_json, original_order_hash) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, orderId);
            ps.setInt(2, userId);
            ps.setString(3, originalJson);
            ps.setString(4, originalHash);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public OrderSignRequest getById(int requestId) {
        String sql = "SELECT * FROM order_sign_requests WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    OrderSignRequest req = new OrderSignRequest();
                    req.setId(rs.getInt("id"));
                    req.setOrderId(rs.getInt("order_id"));
                    req.setUserId(rs.getInt("user_id"));
                    req.setOriginalOrderJson(rs.getString("original_order_json"));
                    req.setOriginalOrderHash(rs.getString("original_order_hash"));
                    req.setDownloadTime(rs.getString("download_time"));
                    return req;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Update the request with JSON containing requestId
    public boolean updateJson(int requestId, String updatedJson, String updatedHash) {
        String sql = "UPDATE order_sign_requests SET original_order_json = ?, original_order_hash = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, updatedJson);
            ps.setString(2, updatedHash);
            ps.setInt(3, requestId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
