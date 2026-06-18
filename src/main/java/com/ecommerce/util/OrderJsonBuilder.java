package com.ecommerce.util;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OrderJsonBuilder {

    public static String buildJson(int orderId, int requestId) throws Exception {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"requestId\": ").append(requestId).append(",\n");
        json.append("  \"orderId\": ").append(orderId).append(",\n");
        
        int userId = 0;
        double totalAmount = 0.0;
        String orderDate = "";
        
        try (Connection con = DBConnection.getConnection()) {
            String sqlOrder = "SELECT user_id, total_amount, order_date FROM orders WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlOrder)) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt("user_id");
                        totalAmount = rs.getDouble("total_amount");
                        orderDate = rs.getString("order_date");
                    } else {
                        throw new Exception("Order not found");
                    }
                }
            }
            
            json.append("  \"userId\": ").append(userId).append(",\n");
            json.append("  \"items\": [\n");
            
            String sqlItems = "SELECT product_id, quantity, price FROM order_items WHERE order_id = ? ORDER BY product_id ASC";
            try (PreparedStatement ps = con.prepareStatement(sqlItems)) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) {
                            json.append(",\n");
                        }
                        json.append("    {");
                        json.append("\"productId\": ").append(rs.getInt("product_id")).append(", ");
                        json.append("\"quantity\": ").append(rs.getInt("quantity")).append(", ");
                        json.append("\"unitPrice\": ").append(rs.getDouble("price"));
                        json.append("}");
                        first = false;
                    }
                }
            }
            json.append("\n  ],\n");
            json.append("  \"totalPrice\": ").append(totalAmount).append(",\n");
            json.append("  \"orderDate\": \"").append(orderDate).append("\"\n");
            json.append("}");
        }
        
        return json.toString();
    }

    public static String computeHash(String json) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(json.getBytes("UTF-8"));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
