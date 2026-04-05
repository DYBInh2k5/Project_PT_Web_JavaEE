package com.project.dao;

import com.project.db.SqlServerConnection;
import com.project.model.PurchaseReceipt;
import com.project.model.PurchaseReceiptItem;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDAO {

    public static class NewPurchaseItem {
        private int maSach;
        private int soLuong;
        private BigDecimal donGia;

        public NewPurchaseItem(int maSach, int soLuong, BigDecimal donGia) {
            this.maSach = maSach;
            this.soLuong = soLuong;
            this.donGia = donGia;
        }

        public int getMaSach() {
            return maSach;
        }

        public int getSoLuong() {
            return soLuong;
        }

        public BigDecimal getDonGia() {
            return donGia;
        }
    }

    public List<PurchaseReceipt> findAll() throws SQLException {
        List<PurchaseReceipt> receipts = new ArrayList<PurchaseReceipt>();
        String sql = "SELECT MaPN, NgayNhap, MaNV, TongTien FROM dbo.PhieuNhap ORDER BY MaPN DESC";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                receipts.add(mapReceipt(rs));
            }
        }

        return receipts;
    }

    public PurchaseReceipt findById(int maPN) throws SQLException {
        String sql = "SELECT MaPN, NgayNhap, MaNV, TongTien FROM dbo.PhieuNhap WHERE MaPN = ?";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maPN);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapReceipt(rs);
                }
            }
        }

        return null;
    }

    public List<PurchaseReceiptItem> findItemsByPurchaseId(int maPN) throws SQLException {
        List<PurchaseReceiptItem> items = new ArrayList<PurchaseReceiptItem>();
        String sql = "SELECT ct.MaCTPN, ct.MaPN, ct.MaSach, ct.SoLuong, ct.DonGia, ct.ThanhTien, s.TenSach "
                + "FROM dbo.ChiTietPhieuNhap ct "
                + "LEFT JOIN dbo.Sach s ON ct.MaSach = s.MaSach "
                + "WHERE ct.MaPN = ? ORDER BY ct.MaCTPN";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maPN);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PurchaseReceiptItem item = new PurchaseReceiptItem();
                    item.setMaCTPN(rs.getInt("MaCTPN"));
                    item.setMaPN(rs.getInt("MaPN"));
                    item.setMaSach(rs.getInt("MaSach"));
                    item.setSoLuong(rs.getInt("SoLuong"));
                    item.setDonGia(rs.getBigDecimal("DonGia"));
                    item.setThanhTien(rs.getBigDecimal("ThanhTien"));
                    item.setTenSach(rs.getString("TenSach"));
                    items.add(item);
                }
            }
        }

        return items;
    }

    public int createPurchase(Integer maNV, List<NewPurchaseItem> items) throws SQLException {
        if (items == null || items.isEmpty()) {
            throw new SQLException("Phieu nhap phai co it nhat 1 dong chi tiet.");
        }

        Connection conn = null;
        try {
            conn = SqlServerConnection.getConnection();
            conn.setAutoCommit(false);

            int maPN = insertHeader(conn, maNV);
            BigDecimal total = BigDecimal.ZERO;

            for (NewPurchaseItem item : items) {
                if (item.getSoLuong() <= 0) {
                    throw new SQLException("So luong phai lon hon 0.");
                }
                if (item.getDonGia() == null || item.getDonGia().compareTo(BigDecimal.ZERO) < 0) {
                    throw new SQLException("Don gia khong hop le cho sach ma " + item.getMaSach());
                }

                BigDecimal thanhTien = item.getDonGia().multiply(BigDecimal.valueOf(item.getSoLuong()));
                insertDetail(conn, maPN, item.getMaSach(), item.getSoLuong(), item.getDonGia(), thanhTien);
                increaseStock(conn, item.getMaSach(), item.getSoLuong());
                total = total.add(thanhTien);
            }

            updateTotal(conn, maPN, total);
            conn.commit();
            return maPN;
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    ex.addSuppressed(rollbackEx);
                }
            }
            throw ex;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignore) {
                }
            }
        }
    }

    private int insertHeader(Connection conn, Integer maNV) throws SQLException {
        String sql = "INSERT INTO dbo.PhieuNhap (NgayNhap, MaNV, TongTien) OUTPUT INSERTED.MaPN VALUES (GETDATE(), ?, 0)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (maNV == null) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, maNV.intValue());
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Khong tao duoc phieu nhap.");
    }

    private void insertDetail(Connection conn, int maPN, int maSach, int soLuong, BigDecimal donGia, BigDecimal thanhTien)
            throws SQLException {
        String sql = "INSERT INTO dbo.ChiTietPhieuNhap (MaPN, MaSach, SoLuong, DonGia, ThanhTien) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maPN);
            ps.setInt(2, maSach);
            ps.setInt(3, soLuong);
            ps.setBigDecimal(4, donGia);
            ps.setBigDecimal(5, thanhTien);
            ps.executeUpdate();
        }
    }

    private void increaseStock(Connection conn, int maSach, int delta) throws SQLException {
        String sql = "UPDATE dbo.Sach SET SoLuong = ISNULL(SoLuong, 0) + ? WHERE MaSach = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, maSach);
            ps.executeUpdate();
        }
    }

    private void updateTotal(Connection conn, int maPN, BigDecimal total) throws SQLException {
        String sql = "UPDATE dbo.PhieuNhap SET TongTien = ? WHERE MaPN = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, total);
            ps.setInt(2, maPN);
            ps.executeUpdate();
        }
    }

    private PurchaseReceipt mapReceipt(ResultSet rs) throws SQLException {
        PurchaseReceipt receipt = new PurchaseReceipt();
        receipt.setMaPN(rs.getInt("MaPN"));
        receipt.setNgayNhap(rs.getTimestamp("NgayNhap"));

        int maNV = rs.getInt("MaNV");
        if (rs.wasNull()) {
            receipt.setMaNV(null);
        } else {
            receipt.setMaNV(maNV);
        }

        receipt.setTongTien(rs.getBigDecimal("TongTien"));
        return receipt;
    }
}