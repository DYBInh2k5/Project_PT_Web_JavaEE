package com.project.dao;

import com.project.db.SqlServerConnection;
import com.project.model.Promotion;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PromotionDAO {

    public List<Promotion> findAll(String keyword) throws SQLException {
        List<Promotion> promotions = new ArrayList<Promotion>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT MaKM, TenKM, HinhThuc, GiaTri, MaCoupon, NgayBD, NgayKT ");
        sql.append("FROM dbo.KhuyenMai ");

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        if (hasKeyword) {
            sql.append("WHERE TenKM LIKE ? OR HinhThuc LIKE ? OR MaCoupon LIKE ? ");
        }

        sql.append("ORDER BY MaKM DESC");

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
                    promotions.add(mapRow(rs));
                }
            }
        }

        return promotions;
    }

    public List<Promotion> findActivePromotions() throws SQLException {
        List<Promotion> promotions = new ArrayList<Promotion>();
        String sql = "SELECT MaKM, TenKM, HinhThuc, GiaTri, MaCoupon, NgayBD, NgayKT "
                + "FROM dbo.KhuyenMai "
                + "WHERE (NgayBD IS NULL OR NgayBD <= CAST(GETDATE() AS date)) "
                + "  AND (NgayKT IS NULL OR NgayKT >= CAST(GETDATE() AS date)) "
                + "ORDER BY MaKM DESC";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                promotions.add(mapRow(rs));
            }
        }

        return promotions;
    }

    public Promotion findByCode(String couponCode) throws SQLException {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT MaKM, TenKM, HinhThuc, GiaTri, MaCoupon, NgayBD, NgayKT "
                + "FROM dbo.KhuyenMai "
                + "WHERE MaCoupon = ? "
                + "  AND (NgayBD IS NULL OR NgayBD <= CAST(GETDATE() AS date)) "
                + "  AND (NgayKT IS NULL OR NgayKT >= CAST(GETDATE() AS date))";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, couponCode.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }

        return null;
    }

    public Promotion findById(int maKM) throws SQLException {
        String sql = "SELECT MaKM, TenKM, HinhThuc, GiaTri, MaCoupon, NgayBD, NgayKT "
                + "FROM dbo.KhuyenMai WHERE MaKM = ?";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maKM);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }

        return null;
    }

    public void insert(Promotion promotion) throws SQLException {
        String sql = "INSERT INTO dbo.KhuyenMai (TenKM, HinhThuc, GiaTri, MaCoupon, NgayBD, NgayKT) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            bindFields(ps, promotion);
            ps.executeUpdate();
        }
    }

    public void update(Promotion promotion) throws SQLException {
        String sql = "UPDATE dbo.KhuyenMai "
                + "SET TenKM = ?, HinhThuc = ?, GiaTri = ?, MaCoupon = ?, NgayBD = ?, NgayKT = ? "
                + "WHERE MaKM = ?";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            bindFields(ps, promotion);
            ps.setInt(7, promotion.getMaKM());
            ps.executeUpdate();
        }
    }

    public void delete(int maKM) throws SQLException {
        String sql = "DELETE FROM dbo.KhuyenMai WHERE MaKM = ?";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maKM);
            ps.executeUpdate();
        }
    }

    public boolean existsCouponCode(String couponCode, Integer excludeMaKM) throws SQLException {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(1) AS Cnt FROM dbo.KhuyenMai WHERE MaCoupon = ?"
                + (excludeMaKM == null ? "" : " AND MaKM <> ?");

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, couponCode.trim());
            if (excludeMaKM != null) {
                ps.setInt(2, excludeMaKM);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Cnt") > 0;
                }
            }
        }

        return false;
    }

    public BigDecimal calculateDiscount(Promotion promotion, BigDecimal subtotal) throws SQLException {
        if (promotion == null) {
            return BigDecimal.ZERO;
        }
        if (subtotal == null) {
            subtotal = BigDecimal.ZERO;
        }

        BigDecimal value = promotion.getGiaTri() == null ? BigDecimal.ZERO : promotion.getGiaTri();
        String type = promotion.getHinhThuc() == null ? "" : promotion.getHinhThuc().trim();

        if ("%".equals(type)) {
            return subtotal.multiply(value).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        }

        if ("COUPON".equalsIgnoreCase(type)) {
            return value;
        }

        throw new SQLException("Khuyen mai nay khong ho tro ap dung vao hoa don.");
    }

    private Promotion mapRow(ResultSet rs) throws SQLException {
        Promotion promotion = new Promotion();
        promotion.setMaKM(rs.getInt("MaKM"));
        promotion.setTenKM(rs.getString("TenKM"));
        promotion.setHinhThuc(rs.getString("HinhThuc"));
        promotion.setGiaTri(rs.getBigDecimal("GiaTri"));
        promotion.setMaCoupon(rs.getString("MaCoupon"));
        promotion.setNgayBD(rs.getDate("NgayBD"));
        promotion.setNgayKT(rs.getDate("NgayKT"));
        return promotion;
    }

    private void bindFields(PreparedStatement ps, Promotion promotion) throws SQLException {
        ps.setString(1, emptyToNull(promotion.getTenKM()));
        ps.setString(2, emptyToNull(promotion.getHinhThuc()));

        if (promotion.getGiaTri() == null) {
            ps.setNull(3, Types.DECIMAL);
        } else {
            ps.setBigDecimal(3, promotion.getGiaTri());
        }

        ps.setString(4, emptyToNull(promotion.getMaCoupon()));

        if (promotion.getNgayBD() == null) {
            ps.setNull(5, Types.DATE);
        } else {
            ps.setDate(5, promotion.getNgayBD());
        }

        if (promotion.getNgayKT() == null) {
            ps.setNull(6, Types.DATE);
        } else {
            ps.setDate(6, promotion.getNgayKT());
        }
    }

    private String emptyToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}