package com.newmeksolutions.dao;

import com.newmeksolutions.model.CustomerChangePayload;
import com.newmeksolutions.config.DBConfig;

import java.sql.*;

public class CustomerDAO {

    public static boolean existsByPhoneOrEmail(String phone, String email) throws Exception {
        String sql = "SELECT 1 FROM customer_table WHERE phonenumber = ? OR email = ?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.setString(2, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static boolean existsByCRMID(int customerID) throws Exception {
        String sql = "SELECT 1 FROM customer_table WHERE customerid = ?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerID);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static void insertCustomerIfNotExists(CustomerChangePayload payload) throws Exception {
        if (existsByPhoneOrEmail(payload.getPhoneNumber(), payload.getEmail())) {
            System.out.println("⚠️ Customer already exists. Skipping insert.");
            return;
        }

        if (isNullOrEmpty(payload.getCustomerName())) {
            String first = payload.getFirstName() != null ? payload.getFirstName().trim() : "";
            String last = payload.getLastName() != null ? payload.getLastName().trim() : "";
            payload.setCustomerName((first + " " + last).trim());
            if (payload.getCustomerName().isEmpty()) payload.setCustomerName("Unknown");
        }

        if (payload.getLastPurchase() == null) {
            payload.setLastPurchase(new Timestamp(System.currentTimeMillis()));
        }

        if (isNullOrEmpty(payload.getDOB()) &&
            isNullOrEmpty(payload.getAddress()) &&
            isNullOrEmpty(payload.getFirstName()) &&
            isNullOrEmpty(payload.getLastName()) &&
            isNullOrEmpty(payload.getPinCode()) &&
            isNullOrEmpty(payload.getState())) {
            payload.setReference("POS_KPHB");
        } else {
            payload.setReference("OnlineReg");
        }

        String sql = "INSERT INTO customer_table (" +
                "customerid, firstname, lastname, customername, phonenumber, " +
                "email, address, location, state, pincode, dob, lastpurchase, \"Reference\"" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, payload.getCustomerID());
            ps.setString(2, payload.getFirstName());
            ps.setString(3, payload.getLastName());
            ps.setString(4, payload.getCustomerName());
            ps.setString(5, payload.getPhoneNumber());
            ps.setString(6, payload.getEmail());
            ps.setString(7, payload.getAddress());
            ps.setString(8, payload.getLocation());
            ps.setString(9, payload.getState());
            ps.setString(10, payload.getPinCode());
            ps.setDate(11, payload.getDOB());
            ps.setTimestamp(12, payload.getLastPurchase());
            ps.setString(13, payload.getReference());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int crmid = generatedKeys.getInt(1);
                    payload.setCRMID(crmid);
                    System.out.println("🆔 Fetched CRMID from DB: " + crmid);
                } else {
                    System.err.println("⚠️ CRMID not returned from DB after insert.");
                }
            }

            System.out.println("✅ Customer inserted into customer_table.");
        }

        payload.setActionType("INSERkT");
        payload.setActionTime(new Timestamp(System.currentTimeMillis()));
        insertCustomerLog(payload);
    }

    public static void updateCustomerByCRMID(CustomerChangePayload payload) throws Exception {
        if (!isCustomerDataChanged(payload)) {
            System.out.println("⚠️ No change in customer data. Skipping update and log.");
            return;
        }

        String sql = "UPDATE customer_table SET " +
                "firstname = ?, lastname = ?, customername = ?, phonenumber = ?, email = ?, " +
                "address = ?, location = ?, state = ?, pincode = ?, dob = ?, lastpurchase = ?, \"Reference\" = ? " +
                "WHERE customerid = ?";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, payload.getFirstName());
            ps.setString(2, payload.getLastName());
            ps.setString(3, payload.getCustomerName());
            ps.setString(4, payload.getPhoneNumber());
            ps.setString(5, payload.getEmail());
            ps.setString(6, payload.getAddress());
            ps.setString(7, payload.getLocation());
            ps.setString(8, payload.getState());
            ps.setString(9, payload.getPinCode());
            ps.setDate(10, payload.getDOB());
            ps.setTimestamp(11, payload.getLastPurchase());
            ps.setString(12, payload.getReference());
            ps.setInt(13, payload.getCustomerID());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("🔄 Customer updated successfully.");

                payload.setActionType("UPDATE");
                payload.setActionTime(new Timestamp(System.currentTimeMillis()));
                fetchAndSetCrmId(payload);
                insertCustomerLog(payload);
            } else {
                System.out.println("⚠️ No customer found to update.");
            }
        }
    }

    public static void deleteCustomerByCRMID(int customerID) throws Exception {
        String fetchSql = "SELECT * FROM customer_table WHERE customerid = ?";
        String deleteSql = "DELETE FROM customer_table WHERE customerid = ?";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement fetchPs = conn.prepareStatement(fetchSql);
             PreparedStatement deletePs = conn.prepareStatement(deleteSql)) {

            fetchPs.setInt(1, customerID);
            ResultSet rs = fetchPs.executeQuery();

            if (rs.next()) {
                CustomerChangePayload payload = new CustomerChangePayload();
                payload.setCRMID(rs.getInt("crmid"));
                payload.setCustomerID(rs.getInt("customerid"));
                payload.setFirstName(rs.getString("firstname"));
                payload.setLastName(rs.getString("lastname"));
                payload.setCustomerName(rs.getString("customername"));
                payload.setPhoneNumber(rs.getString("phonenumber"));
                payload.setEmail(rs.getString("email"));
                payload.setAddress(rs.getString("address"));
                payload.setLocation(rs.getString("location"));
                payload.setState(rs.getString("state"));
                payload.setPinCode(rs.getString("pincode"));
                payload.setDOB(rs.getDate("dob"));
                payload.setLastPurchase(rs.getTimestamp("lastpurchase"));
                payload.setReference(rs.getString("Reference"));
                payload.setActionType("DELETE");
                payload.setActionTime(new Timestamp(System.currentTimeMillis()));

                insertCustomerLog(payload);

                deletePs.setInt(1, customerID);
                int rows = deletePs.executeUpdate();
                System.out.println(rows > 0 ? "❌ Customer deleted successfully." : "⚠️ No customer found to delete.");
            } else {
                System.out.println("⚠️ Customer not found for deletion.");
            }
        }
    }

    public static void insertCustomerLog(CustomerChangePayload payload) throws Exception {
        String logSql = "INSERT INTO customer_log (" +
                "crmid, customerid, firstname, lastname, customername, " +
                "phonenumber, email, address, location, state, pincode, dob, " +
                "lastpurchase, \"Reference\", actiontype, loggedat" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(logSql)) {

            ps.setInt(1, payload.getCRMID());
            ps.setInt(2, payload.getCustomerID());
            ps.setString(3, payload.getFirstName());
            ps.setString(4, payload.getLastName());
            ps.setString(5, payload.getCustomerName());
            ps.setString(6, payload.getPhoneNumber());
            ps.setString(7, payload.getEmail());
            ps.setString(8, payload.getAddress());
            ps.setString(9, payload.getLocation());
            ps.setString(10, payload.getState());
            ps.setString(11, payload.getPinCode());
            ps.setDate(12, payload.getDOB());
            ps.setTimestamp(13, payload.getLastPurchase());
            ps.setString(14, payload.getReference());
            ps.setString(15, payload.getActionType());
            ps.setTimestamp(16, payload.getActionTime());

            ps.executeUpdate();
            System.out.println("📝 Customer inserted into customer_log.");
        }
    }

    public static boolean logExistsByCRMID(int crmId) throws Exception {
        String sql = "SELECT 1 FROM customer_log WHERE crmid = ?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, crmId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static void fetchAndSetCrmId(CustomerChangePayload payload) throws Exception {
        String sql = "SELECT crmid FROM customer_table WHERE customerid = ?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, payload.getCustomerID());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    payload.setCRMID(rs.getInt("crmid"));
                }
            }
        }
    }

    private static boolean isCustomerDataChanged(CustomerChangePayload payload) throws Exception {
        String sql = "SELECT * FROM customer_table WHERE customerid = ?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, payload.getCustomerID());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return !(safeEquals(payload.getFirstName(), rs.getString("firstname")) &&
                         safeEquals(payload.getLastName(), rs.getString("lastname")) &&
                         safeEquals(payload.getCustomerName(), rs.getString("customername")) &&
                         safeEquals(payload.getPhoneNumber(), rs.getString("phonenumber")) &&
                         safeEquals(payload.getEmail(), rs.getString("email")) &&
                         safeEquals(payload.getAddress(), rs.getString("address")) &&
                         safeEquals(payload.getLocation(), rs.getString("location")) &&
                         safeEquals(payload.getState(), rs.getString("state")) &&
                         safeEquals(payload.getPinCode(), rs.getString("pincode")) &&
                         safeEquals(payload.getDOB(), rs.getDate("dob")) &&
                         safeEquals(payload.getLastPurchase(), rs.getTimestamp("lastpurchase")));
            }
        }
        return true;
    }

    private static boolean safeEquals(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    private static boolean safeEquals(java.sql.Date a, java.sql.Date b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    private static boolean safeEquals(Timestamp a, Timestamp b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static boolean isNullOrEmpty(java.sql.Date d) {
        return d == null;
    }
}