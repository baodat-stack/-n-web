package com.ecommerce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ecommerce.model.VerificationLog;
import com.ecommerce.util.DBConnection;

public class VerificationLogDAO {

    public boolean log(int orderId, int userId, String result, String message) {
        String sql = "INSERT INTO verification_logs (order_id, user_id, result, message) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, userId);
            ps.setString(3, result);
            ps.setString(4, message);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<VerificationLog> getAll() {
        List<VerificationLog> list = new ArrayList<>();
        String sql = "SELECT * FROM verification_logs ORDER BY verify_time DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                VerificationLog v = new VerificationLog();
                v.setId(rs.getInt("id"));
                v.setOrderId(rs.getInt("order_id"));
                v.setUserId(rs.getInt("user_id"));
                v.setVerifyTime(rs.getString("verify_time"));
                v.setResult(rs.getString("result"));
                v.setMessage(rs.getString("message"));
                list.add(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
