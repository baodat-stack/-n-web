package com.ecommerce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ecommerce.model.Product;
import com.ecommerce.util.DBConnection;

public class ProductDAO {

    // Helper to build a Product from ResultSet
    private Product buildProduct(ResultSet rs) throws Exception {
        Product p = new Product(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getString("category"),
            rs.getDouble("price"),
            rs.getString("image")
        );
        p.setStock(rs.getInt("stock"));
        return p;
    }

    public boolean addProduct(Product product) {
        boolean isSuccess = false;
        String query = "INSERT INTO products (name, description, category, price, image, stock) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, product.getName());
            pst.setString(2, product.getDescription());
            pst.setString(3, product.getCategory());
            pst.setDouble(4, product.getPrice());
            pst.setString(5, product.getImage());
            pst.setInt(6, product.getStock());
            
            int rowCount = pst.executeUpdate();
            if (rowCount > 0) isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                products.add(buildProduct(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    public boolean updateProduct(Product product) {
        boolean isSuccess = false;
        String query = "UPDATE products SET name=?, description=?, category=?, price=?, image=?, stock=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, product.getName());
            pst.setString(2, product.getDescription());
            pst.setString(3, product.getCategory());
            pst.setDouble(4, product.getPrice());
            pst.setString(5, product.getImage());
            pst.setInt(6, product.getStock());
            pst.setInt(7, product.getId());
            
            int rowCount = pst.executeUpdate();
            if (rowCount > 0) isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public boolean deleteProduct(int id) {
        boolean isSuccess = false;
        String query = "DELETE FROM products WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, id);
            int rowCount = pst.executeUpdate();
            if (rowCount > 0) isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public Product getProductById(int id) {
        Product row = null;
        String query = "SELECT * FROM products WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                row = buildProduct(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return row;
    }

    public List<Product> searchProducts(String searchKeyword) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products WHERE name LIKE ? OR category LIKE ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            String keyword = "%" + searchKeyword + "%";
            pst.setString(1, keyword);
            pst.setString(2, keyword);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                products.add(buildProduct(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    // ========== PAGINATION ==========

    public List<Product> getProductsPaginated(int page, int pageSize) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products ORDER BY id DESC LIMIT ? OFFSET ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, pageSize);
            pst.setInt(2, (page - 1) * pageSize);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                products.add(buildProduct(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    public int getProductCount() {
        int count = 0;
        String query = "SELECT COUNT(*) FROM products";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            ResultSet rs = pst.executeQuery();
            if (rs.next()) count = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    // ========== CATEGORY FILTERING ==========

    public List<Product> getProductsByCategory(String category, int page, int pageSize) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products WHERE category=? ORDER BY id DESC LIMIT ? OFFSET ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, category);
            pst.setInt(2, pageSize);
            pst.setInt(3, (page - 1) * pageSize);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                products.add(buildProduct(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    public int getProductCountByCategory(String category) {
        int count = 0;
        String query = "SELECT COUNT(*) FROM products WHERE category=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, category);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) count = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String query = "SELECT DISTINCT category FROM products ORDER BY category";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categories;
    }

    // ========== STOCK MANAGEMENT ==========

    public void reduceStock(Connection con, int productId, int quantity) throws Exception {
        String query = "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, quantity);
            pst.setInt(2, productId);
            pst.setInt(3, quantity);
            int rows = pst.executeUpdate();
            if (rows == 0) {
                throw new Exception("Insufficient stock for product ID: " + productId);
            }
        }
    }
}
