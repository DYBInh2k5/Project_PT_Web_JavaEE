package com.project.dao;

import com.project.db.SqlServerConnection;
import com.project.model.dto.RevenueByDate;
import com.project.model.dto.TopBookReportItem;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    public List<RevenueByDate> findRevenueByDate(Date fromDate, Date toDate) throws SQLException {
        List<RevenueByDate> rows = new ArrayList<RevenueByDate>();
        String sql = "SELECT CAST(hd.NgayLap AS date) AS Ngay, ISNULL(SUM(hd.TongTien), 0) AS DoanhThu "
                + "FROM dbo.HoaDon hd "
                + "WHERE (? IS NULL OR CAST(hd.NgayLap AS date) >= ?) "
                + "AND (? IS NULL OR CAST(hd.NgayLap AS date) <= ?) "
                + "GROUP BY CAST(hd.NgayLap AS date) "
                + "ORDER BY Ngay";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            bindDate(ps, 1, fromDate);
            bindDate(ps, 2, fromDate);
            bindDate(ps, 3, toDate);
            bindDate(ps, 4, toDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RevenueByDate item = new RevenueByDate();
                    item.setNgay(rs.getDate("Ngay"));
                    item.setDoanhThu(rs.getBigDecimal("DoanhThu"));
                    rows.add(item);
                }
            }
        }

        return rows;
    }

    public List<TopBookReportItem> findTopBooks(Date fromDate, Date toDate, int topN) throws SQLException {
        List<TopBookReportItem> rows = new ArrayList<TopBookReportItem>();
        String sql = "SELECT TOP (?) s.MaSach, s.TenSach, SUM(ISNULL(ct.SoLuong, 0)) AS TongSoLuong, "
                + "SUM(ISNULL(ct.ThanhTien, 0)) AS TongDoanhThu "
                + "FROM dbo.ChiTietHoaDon ct "
                + "INNER JOIN dbo.HoaDon hd ON ct.MaHD = hd.MaHD "
                + "INNER JOIN dbo.Sach s ON ct.MaSach = s.MaSach "
                + "WHERE (? IS NULL OR CAST(hd.NgayLap AS date) >= ?) "
                + "AND (? IS NULL OR CAST(hd.NgayLap AS date) <= ?) "
                + "GROUP BY s.MaSach, s.TenSach "
                + "ORDER BY TongSoLuong DESC, TongDoanhThu DESC";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, topN);
            bindDate(ps, 2, fromDate);
            bindDate(ps, 3, fromDate);
            bindDate(ps, 4, toDate);
            bindDate(ps, 5, toDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TopBookReportItem item = new TopBookReportItem();
                    item.setMaSach(rs.getInt("MaSach"));
                    item.setTenSach(rs.getString("TenSach"));
                    item.setTongSoLuong(rs.getInt("TongSoLuong"));
                    BigDecimal total = rs.getBigDecimal("TongDoanhThu");
                    item.setTongDoanhThu(total == null ? BigDecimal.ZERO : total);
                    rows.add(item);
                }
            }
        }

        return rows;
    }

    private void bindDate(PreparedStatement ps, int index, Date value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.DATE);
        } else {
            ps.setDate(index, value);
        }
    }
}
