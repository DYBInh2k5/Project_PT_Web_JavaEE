package com.project.dao;

import com.project.db.SqlServerConnection;
import com.project.model.dto.DashboardStats;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardDAO {

    public DashboardStats loadStats() throws SQLException {
        DashboardStats stats = new DashboardStats();

        try (Connection conn = SqlServerConnection.getConnection()) {
            stats.setTotalBooks(queryInt(conn, "SELECT COUNT(*) FROM dbo.Sach"));
            stats.setTotalCustomers(queryInt(conn, "SELECT COUNT(*) FROM dbo.KhachHang"));
            stats.setTotalInvoices(queryInt(conn, "SELECT COUNT(*) FROM dbo.HoaDon"));
            stats.setRevenue(queryDecimal(conn, "SELECT ISNULL(SUM(TongTien), 0) FROM dbo.HoaDon"));
        }

        return stats;
    }

    private int queryInt(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private BigDecimal queryDecimal(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                BigDecimal value = rs.getBigDecimal(1);
                return value == null ? BigDecimal.ZERO : value;
            }
        }
        return BigDecimal.ZERO;
    }
}
