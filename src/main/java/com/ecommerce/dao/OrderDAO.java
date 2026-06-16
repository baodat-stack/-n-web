package com.ecommerce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ecommerce.model.Cart;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.util.DBConnection;

public class OrderDAO {

    public boolean placeOrder(int userId, List<Cart> cartList, int addressId, String paymentMethod) {
        boolean isSuccess = false;
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // Enable transaction

            // Calculate total amount
            double totalAmount = 0.0;
            for (Cart c : cartList) {
                totalAmount += c.getProduct().getPrice() * c.getQuantity();
            }

            // Insert into orders table (with address_id and payment_method)
            String orderQuery = "INSERT INTO orders (user_id, total_amount, status, address_id, payment_method) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement orderPst = con.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
            orderPst.setInt(1, userId);
            orderPst.setDouble(2, totalAmount);
            orderPst.setString(3, "Processing");
            orderPst.setInt(4, addressId);
            orderPst.setString(5, paymentMethod != null ? paymentMethod : "COD");
            orderPst.executeUpdate();

            // Get generated order ID
            ResultSet rs = orderPst.getGeneratedKeys();
            int orderId = 0;
            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            // Insert into order_items table
            String itemQuery = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
            PreparedStatement itemPst = con.prepareStatement(itemQuery);
            for (Cart c : cartList) {
                itemPst.setInt(1, orderId);
                itemPst.setInt(2, c.getProductId());
                itemPst.setInt(3, c.getQuantity());
                itemPst.setDouble(4, c.getProduct().getPrice());
                itemPst.addBatch();
            }
            itemPst.executeBatch();

            // Reduce stock for each product
            ProductDAO productDAO = new ProductDAO();
            for (Cart c : cartList) {
                productDAO.reduceStock(con, c.getProductId(), c.getQuantity());
            }

            // Clear the user's cart
            String clearCartQuery = "DELETE FROM cart WHERE user_id=?";
            PreparedStatement clearCartPst = con.prepareStatement(clearCartQuery);
            clearCartPst.setInt(1, userId);
            clearCartPst.executeUpdate();

            con.commit(); // Commit transaction
            isSuccess = true;
        } catch (Exception e) {
            try {
                if (con != null) con.rollback(); // Rollback on failure
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isSuccess;
    }

    // Keep old method signature for backward compatibility (default address 0, COD)
    public boolean placeOrder(int userId, List<Cart> cartList) {
        return placeOrder(userId, cartList, 0, "COD");
    }

    public Order getOrderById(int orderId) {
        Order order = null;
        String query = "SELECT * FROM orders WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, orderId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                order = new Order();
                order.setId(rs.getInt("id"));
                order.setUserId(rs.getInt("user_id"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                order.setStatus(rs.getString("status"));
                order.setAddressId(rs.getInt("address_id"));
                order.setPaymentMethod(rs.getString("payment_method"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return order;
    }

    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        ProductDAO productDAO = new ProductDAO();
        String query = "SELECT * FROM order_items WHERE order_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, orderId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setId(rs.getInt("id"));
                item.setOrderId(rs.getInt("order_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPrice(rs.getDouble("price"));
                // Attach product details
                item.setProduct(productDAO.getProductById(item.getProductId()));
                items.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<Order> getUserOrders(int userId) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders WHERE user_id=? ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Order order = new Order(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getDouble("total_amount"),
                    rs.getTimestamp("order_date"),
                    rs.getString("status")
                );
                order.setAddressId(rs.getInt("address_id"));
                order.setPaymentMethod(rs.getString("payment_method"));
                orders.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Order order = new Order(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getDouble("total_amount"),
                    rs.getTimestamp("order_date"),
                    rs.getString("status")
                );
                order.setAddressId(rs.getInt("address_id"));
                order.setPaymentMethod(rs.getString("payment_method"));
                orders.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }

    public boolean updateOrderStatus(int orderId, String status) {
        boolean isSuccess = false;
        String query = "UPDATE orders SET status=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, status);
            pst.setInt(2, orderId);
            int rowCount = pst.executeUpdate();
            if (rowCount > 0) isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public double getTotalRevenue() {
        double total = 0;
        String query = "SELECT COALESCE(SUM(total_amount), 0) FROM orders";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                total = rs.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    public int getTotalOrderCount() {
        int count = 0;
        String query = "SELECT COUNT(*) FROM orders";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
}
