package com.ecommerce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ecommerce.model.AuditLog;
import com.ecommerce.util.AuditAction;
import com.ecommerce.util.DBConnection;

public class AuditLogDAO {

    public boolean log(int userId, int orderId, AuditAction action, String description) {
        String sql = "INSERT INTO audit_logs (user_id, order_id, action_type, description) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (userId > 0) ps.setInt(1, userId); else ps.setNull(1, java.sql.Types.INTEGER);
            if (orderId > 0) ps.setInt(2, orderId); else ps.setNull(2, java.sql.Types.INTEGER);
            ps.setString(3, action.name());
            ps.setString(4, description);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<AuditLog> getAll(String actionType) {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs";
        if (actionType != null && !actionType.isEmpty()) {
            sql += " WHERE action_type = ?";
        }
        sql += " ORDER BY created_at DESC";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (actionType != null && !actionType.isEmpty()) {
                ps.setString(1, actionType);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AuditLog a = new AuditLog();
                    a.setId(rs.getInt("id"));
                    a.setUserId(rs.getInt("user_id"));
                    a.setOrderId(rs.getInt("order_id"));
                    a.setActionType(AuditAction.valueOf(rs.getString("action_type")));
                    a.setDescription(rs.getString("description"));
                    a.setCreatedAt(rs.getString("created_at"));
                    list.add(a);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
