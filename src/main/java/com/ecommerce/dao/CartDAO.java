package com.ecommerce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ecommerce.model.Cart;
import com.ecommerce.util.DBConnection;

public class CartDAO {
    private ProductDAO productDAO;

    public CartDAO() {
        productDAO = new ProductDAO();
    }

    public boolean addToCart(Cart cart) {
        boolean isSuccess = false;
        
        // Check if product already exists in cart for this user
        String checkQuery = "SELECT * FROM cart WHERE user_id=? AND product_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement checkPst = con.prepareStatement(checkQuery)) {
            checkPst.setInt(1, cart.getUserId());
            checkPst.setInt(2, cart.getProductId());
            ResultSet rs = checkPst.executeQuery();
            
            if (rs.next()) {
                // If exists, update quantity
                int existingQty = rs.getInt("quantity");
                int newQty = existingQty + cart.getQuantity();
                return updateCartQuantity(rs.getInt("id"), newQty);
            } else {
                // Insert new row
                String insertQuery = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?)";
                try (PreparedStatement insertPst = con.prepareStatement(insertQuery)) {
                    insertPst.setInt(1, cart.getUserId());
                    insertPst.setInt(2, cart.getProductId());
                    insertPst.setInt(3, cart.getQuantity());
                    int rowCount = insertPst.executeUpdate();
                    if (rowCount > 0) isSuccess = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public List<Cart> getCartItemsByUser(int userId) {
        List<Cart> cartList = new ArrayList<>();
        String query = "SELECT * FROM cart WHERE user_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Cart row = new Cart();
                row.setId(rs.getInt("id"));
                row.setUserId(rs.getInt("user_id"));
                row.setProductId(rs.getInt("product_id"));
                row.setQuantity(rs.getInt("quantity"));
                
                // Get product details
                row.setProduct(productDAO.getProductById(row.getProductId()));
                
                cartList.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cartList;
    }

    public boolean updateCartQuantity(int cartId, int quantity) {
        boolean isSuccess = false;
        String query = "UPDATE cart SET quantity=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, quantity);
            pst.setInt(2, cartId);
            int rowCount = pst.executeUpdate();
            if (rowCount > 0) isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public boolean removeCartItem(int cartId) {
        boolean isSuccess = false;
        String query = "DELETE FROM cart WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, cartId);
            int rowCount = pst.executeUpdate();
            if (rowCount > 0) isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
}
