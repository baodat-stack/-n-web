package com.ecommerce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ecommerce.model.UserKey;
import com.ecommerce.model.KeyHistory;
import com.ecommerce.util.DBConnection;

public class UserKeyDAO {

    public int saveKey(int userId, String publicKeyBase64, String fingerprint) {
        String sql = "INSERT INTO user_keys (user_id, public_key, fingerprint, status) VALUES (?, ?, ?, 'ACTIVE')";
        String historySql = "INSERT INTO key_history (user_id, key_id, action) VALUES (?, ?, 'GENERATED')";
        
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int keyId = -1;
                try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, userId);
                    ps.setString(2, publicKeyBase64);
                    ps.setString(3, fingerprint);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) keyId = rs.getInt(1);
                    }
                }
                
                if (keyId != -1) {
                    try (PreparedStatement ps = con.prepareStatement(historySql)) {
                        ps.setInt(1, userId);
                        ps.setInt(2, keyId);
                        ps.executeUpdate();
                    }
                }
                
                con.commit();
                return keyId;
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public UserKey getActiveKey(int userId) {
        String sql = "SELECT * FROM user_keys WHERE user_id = ? AND status = 'ACTIVE'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserKey key = new UserKey();
                    key.setId(rs.getInt("id"));
                    key.setUserId(rs.getInt("user_id"));
                    key.setPublicKey(rs.getString("public_key"));
                    key.setFingerprint(rs.getString("fingerprint"));
                    key.setIssueDate(rs.getString("issue_date"));
                    key.setRevokeDate(rs.getString("revoke_date"));
                    key.setStatus(rs.getString("status"));
                    return key;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserKey getKeyById(int keyId) {
        String sql = "SELECT * FROM user_keys WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, keyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserKey key = new UserKey();
                    key.setId(rs.getInt("id"));
                    key.setUserId(rs.getInt("user_id"));
                    key.setPublicKey(rs.getString("public_key"));
                    key.setFingerprint(rs.getString("fingerprint"));
                    key.setIssueDate(rs.getString("issue_date"));
                    key.setRevokeDate(rs.getString("revoke_date"));
                    key.setStatus(rs.getString("status"));
                    return key;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean revokeKey(int keyId) {
        String sql = "UPDATE user_keys SET status = 'REVOKED', revoke_date = NOW() WHERE id = ?";
        String historySql = "INSERT INTO key_history (user_id, key_id, action) SELECT user_id, id, 'REVOKED' FROM user_keys WHERE id = ?";
        
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, keyId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(historySql)) {
                    ps.setInt(1, keyId);
                    ps.executeUpdate();
                }
                con.commit();
                return true;
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasActiveKey(int userId) {
        String sql = "SELECT COUNT(*) FROM user_keys WHERE user_id = ? AND status = 'ACTIVE'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<KeyHistory> getKeyHistory(int userId) {
        List<KeyHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM key_history WHERE user_id = ? ORDER BY action_date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    KeyHistory h = new KeyHistory();
                    h.setId(rs.getInt("id"));
                    h.setUserId(rs.getInt("user_id"));
                    h.setKeyId(rs.getInt("key_id"));
                    h.setAction(rs.getString("action"));
                    h.setActionDate(rs.getString("action_date"));
                    list.add(h);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
