package com.ecommerce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ecommerce.model.Address;
import com.ecommerce.util.DBConnection;

public class AddressDAO {

    public boolean addAddress(Address address) {
        boolean isSuccess = false;
        // If this is the first address for the user, make it default
        List<Address> existing = getAddressesByUser(address.getUserId());
        if (existing.isEmpty()) {
            address.setDefault(true);
        }

        String query = "INSERT INTO addresses (user_id, full_name, phone, address_line, city, pincode, is_default) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, address.getUserId());
            pst.setString(2, address.getFullName());
            pst.setString(3, address.getPhone());
            pst.setString(4, address.getAddressLine());
            pst.setString(5, address.getCity());
            pst.setString(6, address.getPincode());
            pst.setBoolean(7, address.isDefault());
            int rowCount = pst.executeUpdate();
            if (rowCount > 0) isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public List<Address> getAddressesByUser(int userId) {
        List<Address> addresses = new ArrayList<>();
        String query = "SELECT * FROM addresses WHERE user_id=? ORDER BY is_default DESC, id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Address addr = new Address();
                addr.setId(rs.getInt("id"));
                addr.setUserId(rs.getInt("user_id"));
                addr.setFullName(rs.getString("full_name"));
                addr.setPhone(rs.getString("phone"));
                addr.setAddressLine(rs.getString("address_line"));
                addr.setCity(rs.getString("city"));
                addr.setPincode(rs.getString("pincode"));
                addr.setDefault(rs.getBoolean("is_default"));
                addresses.add(addr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return addresses;
    }

    public Address getAddressById(int addressId) {
        Address addr = null;
        String query = "SELECT * FROM addresses WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, addressId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                addr = new Address();
                addr.setId(rs.getInt("id"));
                addr.setUserId(rs.getInt("user_id"));
                addr.setFullName(rs.getString("full_name"));
                addr.setPhone(rs.getString("phone"));
                addr.setAddressLine(rs.getString("address_line"));
                addr.setCity(rs.getString("city"));
                addr.setPincode(rs.getString("pincode"));
                addr.setDefault(rs.getBoolean("is_default"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return addr;
    }

    public boolean deleteAddress(int addressId) {
        boolean isSuccess = false;
        String query = "DELETE FROM addresses WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, addressId);
            int rowCount = pst.executeUpdate();
            if (rowCount > 0) isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public boolean setDefaultAddress(int userId, int addressId) {
        boolean isSuccess = false;
        try (Connection con = DBConnection.getConnection()) {
            // First, unset all defaults for this user
            String unsetQuery = "UPDATE addresses SET is_default=FALSE WHERE user_id=?";
            try (PreparedStatement pst = con.prepareStatement(unsetQuery)) {
                pst.setInt(1, userId);
                pst.executeUpdate();
            }
            // Set the chosen address as default
            String setQuery = "UPDATE addresses SET is_default=TRUE WHERE id=? AND user_id=?";
            try (PreparedStatement pst = con.prepareStatement(setQuery)) {
                pst.setInt(1, addressId);
                pst.setInt(2, userId);
                int rowCount = pst.executeUpdate();
                if (rowCount > 0) isSuccess = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
}
