package com.project.dao;

import com.project.db.SqlServerConnection;
import com.project.model.Customer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public List<Customer> findAll(String keyword) throws SQLException {
        List<Customer> customers = new ArrayList<Customer>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT MaKH, TenKH, DienThoai, Email, DiaChi FROM dbo.KhachHang ");

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        if (hasKeyword) {
            sql.append("WHERE TenKH LIKE ? OR DienThoai LIKE ? OR Email LIKE ? ");
        }

        sql.append("ORDER BY MaKH DESC");

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            if (hasKeyword) {
                String q = "%" + keyword.trim() + "%";
                ps.setString(1, q);
                ps.setString(2, q);
                ps.setString(3, q);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapRow(rs));
                }
            }
        }

        return customers;
    }

    public Customer findById(int maKH) throws SQLException {
        String sql = "SELECT MaKH, TenKH, DienThoai, Email, DiaChi FROM dbo.KhachHang WHERE MaKH = ?";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maKH);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }

        return null;
    }

    public Customer findByPhoneOrEmail(String phone, String email) throws SQLException {
        boolean hasPhone = phone != null && !phone.trim().isEmpty();
        boolean hasEmail = email != null && !email.trim().isEmpty();
        if (!hasPhone && !hasEmail) {
            return null;
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT TOP 1 MaKH, TenKH, DienThoai, Email, DiaChi FROM dbo.KhachHang WHERE ");
        if (hasPhone && hasEmail) {
            sql.append("DienThoai = ? OR Email = ?");
        } else if (hasPhone) {
            sql.append("DienThoai = ?");
        } else {
            sql.append("Email = ?");
        }
        sql.append(" ORDER BY MaKH DESC");

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (hasPhone && hasEmail) {
                ps.setString(idx++, phone.trim());
                ps.setString(idx, email.trim());
            } else if (hasPhone) {
                ps.setString(idx, phone.trim());
            } else {
                ps.setString(idx, email.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }

        return null;
    }

    public void insert(Customer customer) throws SQLException {
        String sql = "INSERT INTO dbo.KhachHang (TenKH, DienThoai, Email, DiaChi) VALUES (?, ?, ?, ?)";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            bindFields(ps, customer);
            ps.executeUpdate();
        }
    }

    public int insertAndGetId(Customer customer) throws SQLException {
        String sql = "INSERT INTO dbo.KhachHang (TenKH, DienThoai, Email, DiaChi) OUTPUT INSERTED.MaKH VALUES (?, ?, ?, ?)";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            bindFields(ps, customer);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Khong tao duoc khach hang moi.");
    }

    public void update(Customer customer) throws SQLException {
        String sql = "UPDATE dbo.KhachHang SET TenKH = ?, DienThoai = ?, Email = ?, DiaChi = ? WHERE MaKH = ?";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            bindFields(ps, customer);
            ps.setInt(5, customer.getMaKH());
            ps.executeUpdate();
        }
    }

    public void delete(int maKH) throws SQLException {
        String sql = "DELETE FROM dbo.KhachHang WHERE MaKH = ?";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maKH);
            ps.executeUpdate();
        }
    }

    private void bindFields(PreparedStatement ps, Customer customer) throws SQLException {
        ps.setString(1, emptyToNull(customer.getTenKH()));
        ps.setString(2, emptyToNull(customer.getDienThoai()));
        ps.setString(3, emptyToNull(customer.getEmail()));
        ps.setString(4, emptyToNull(customer.getDiaChi()));
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setMaKH(rs.getInt("MaKH"));
        c.setTenKH(rs.getString("TenKH"));
        c.setDienThoai(rs.getString("DienThoai"));
        c.setEmail(rs.getString("Email"));
        c.setDiaChi(rs.getString("DiaChi"));
        return c;
    }

    private String emptyToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
