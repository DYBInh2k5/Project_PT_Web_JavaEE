package com.project.dao;

import com.project.db.SqlServerConnection;
import com.project.model.InvoiceItem;
import com.project.model.ReturnItem;
import com.project.model.ReturnTransaction;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReturnDAO {

    public static class NewReturnItem {
        private int maSach;
        private int soLuong;

        public NewReturnItem(int maSach, int soLuong) {
            this.maSach = maSach;
            this.soLuong = soLuong;
        }

        public int getMaSach() {
            return maSach;
        }

        public int getSoLuong() {
            return soLuong;
        }
    }

    public List<ReturnTransaction> findAll() throws SQLException {
        List<ReturnTransaction> returns = new ArrayList<ReturnTransaction>();
        String sql = "SELECT dt.MaDT, dt.MaHD, dt.NgayDoi, dt.LyDo, dt.GhiChu, dt.KieuXuLy, hd.TongTien, kh.TenKH "
                + "FROM dbo.DoiTra dt "
                + "LEFT JOIN dbo.HoaDon hd ON dt.MaHD = hd.MaHD "
                + "LEFT JOIN dbo.KhachHang kh ON hd.MaKH = kh.MaKH "
                + "ORDER BY dt.MaDT DESC";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                returns.add(mapHeader(rs));
            }
        }

        return returns;
    }

    public ReturnTransaction findById(int maDT) throws SQLException {
        String sql = "SELECT dt.MaDT, dt.MaHD, dt.NgayDoi, dt.LyDo, dt.GhiChu, dt.KieuXuLy, hd.TongTien, kh.TenKH "
                + "FROM dbo.DoiTra dt "
                + "LEFT JOIN dbo.HoaDon hd ON dt.MaHD = hd.MaHD "
                + "LEFT JOIN dbo.KhachHang kh ON hd.MaKH = kh.MaKH "
                + "WHERE dt.MaDT = ?";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maDT);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapHeader(rs);
                }
            }
        }

        return null;
    }

    public List<ReturnItem> findItemsByReturnId(int maDT) throws SQLException {
        List<ReturnItem> items = new ArrayList<ReturnItem>();
        String sql = "SELECT ct.MaCTDT, ct.MaDT, ct.MaSach, ct.SoLuong, ct.DonGia, ct.ThanhTien, s.TenSach "
                + "FROM dbo.ChiTietDoiTra ct "
                + "LEFT JOIN dbo.Sach s ON ct.MaSach = s.MaSach "
                + "WHERE ct.MaDT = ? ORDER BY ct.MaCTDT";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maDT);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReturnItem item = new ReturnItem();
                    item.setMaCTDT(rs.getInt("MaCTDT"));
                    item.setMaDT(rs.getInt("MaDT"));
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

    public List<InvoiceItem> findInvoiceItems(int maHD) throws SQLException {
        List<InvoiceItem> items = new ArrayList<InvoiceItem>();
        String sql = "SELECT ct.MaCT, ct.MaHD, ct.MaSach, ct.SoLuong, ct.DonGia, ct.ThanhTien, s.TenSach "
                + "FROM dbo.ChiTietHoaDon ct "
                + "LEFT JOIN dbo.Sach s ON ct.MaSach = s.MaSach "
                + "WHERE ct.MaHD = ? ORDER BY ct.MaCT";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InvoiceItem item = new InvoiceItem();
                    item.setMaCT(rs.getInt("MaCT"));
                    item.setMaHD(rs.getInt("MaHD"));
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

    public int createReturn(int maHD, String lyDo, String ghiChu, int kieuXuLy, List<NewReturnItem> items)
            throws SQLException {

        if (items == null || items.isEmpty()) {
            throw new SQLException("Phai co it nhat 1 dong chi tiet doi tra.");
        }

        Connection conn = null;
        try {
            conn = SqlServerConnection.getConnection();
            conn.setAutoCommit(false);

            int maDT = insertHeader(conn, maHD, lyDo, ghiChu, kieuXuLy);
            for (NewReturnItem item : items) {
                if (item.getSoLuong() <= 0) {
                    throw new SQLException("So luong doi tra phai lon hon 0.");
                }

                ReturnStockInfo stockInfo = getInvoiceStockInfoForUpdate(conn, maHD, item.getMaSach());
                if (stockInfo == null) {
                    throw new SQLException("Khong tim thay sach ma " + item.getMaSach() + " trong hoa don " + maHD);
                }

                int available = stockInfo.getSoldQty() - stockInfo.getReturnedQty();
                if (item.getSoLuong() > available) {
                    throw new SQLException("So luong doi tra vuot qua so luong con co the doi tra cho sach ma " + item.getMaSach());
                }

                BigDecimal thanhTien = stockInfo.getUnitPrice().multiply(BigDecimal.valueOf(item.getSoLuong()));
                insertDetail(conn, maDT, item.getMaSach(), item.getSoLuong(), stockInfo.getUnitPrice(), thanhTien);
                updateStock(conn, item.getMaSach(), stockInfo.getCurrentStock() + item.getSoLuong());
            }

            conn.commit();
            return maDT;
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

    private int insertHeader(Connection conn, int maHD, String lyDo, String ghiChu, int kieuXuLy) throws SQLException {
        String sql = "INSERT INTO dbo.DoiTra (MaHD, NgayDoi, LyDo, GhiChu, KieuXuLy) OUTPUT INSERTED.MaDT VALUES (?, GETDATE(), ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, maHD);
            ps.setString(2, emptyToNull(lyDo));
            ps.setString(3, emptyToNull(ghiChu));
            ps.setInt(4, kieuXuLy);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Khong tao duoc phieu doi tra.");
    }

    private void insertDetail(Connection conn, int maDT, int maSach, int soLuong, BigDecimal donGia, BigDecimal thanhTien)
            throws SQLException {
        String sql = "INSERT INTO dbo.ChiTietDoiTra (MaDT, MaSach, SoLuong, DonGia, ThanhTien) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maDT);
            ps.setInt(2, maSach);
            ps.setInt(3, soLuong);
            ps.setBigDecimal(4, donGia);
            ps.setBigDecimal(5, thanhTien);
            ps.executeUpdate();
        }
    }

    private void updateStock(Connection conn, int maSach, int newStock) throws SQLException {
        String sql = "UPDATE dbo.Sach SET SoLuong = ? WHERE MaSach = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newStock);
            ps.setInt(2, maSach);
            ps.executeUpdate();
        }
    }

    private ReturnStockInfo getInvoiceStockInfoForUpdate(Connection conn, int maHD, int maSach) throws SQLException {
        String sql = "SELECT ct.SoLuong AS SoldQty, ct.DonGia, s.SoLuong AS CurrentStock, "
                + "ISNULL(r.ReturnedQty, 0) AS ReturnedQty "
                + "FROM dbo.ChiTietHoaDon ct "
                + "INNER JOIN dbo.Sach s WITH (UPDLOCK, ROWLOCK) ON ct.MaSach = s.MaSach "
                + "LEFT JOIN (SELECT dt.MaHD, ctdt.MaSach, SUM(ctdt.SoLuong) AS ReturnedQty "
                + "           FROM dbo.DoiTra dt "
                + "           INNER JOIN dbo.ChiTietDoiTra ctdt ON dt.MaDT = ctdt.MaDT "
                + "           GROUP BY dt.MaHD, ctdt.MaSach) r "
                + "       ON r.MaHD = ct.MaHD AND r.MaSach = ct.MaSach "
                + "WHERE ct.MaHD = ? AND ct.MaSach = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maHD);
            ps.setInt(2, maSach);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int soldQty = rs.getInt("SoldQty");
                    int returnedQty = rs.getInt("ReturnedQty");
                    int currentStock = rs.getInt("CurrentStock");
                    BigDecimal unitPrice = rs.getBigDecimal("DonGia");
                    if (unitPrice == null) {
                        unitPrice = BigDecimal.ZERO;
                    }
                    return new ReturnStockInfo(soldQty, returnedQty, currentStock, unitPrice);
                }
            }
        }

        return null;
    }

    private ReturnTransaction mapHeader(ResultSet rs) throws SQLException {
        ReturnTransaction tx = new ReturnTransaction();
        tx.setMaDT(rs.getInt("MaDT"));
        tx.setMaHD(rs.getInt("MaHD"));
        tx.setNgayDoi(rs.getTimestamp("NgayDoi"));
        tx.setLyDo(rs.getString("LyDo"));
        tx.setGhiChu(rs.getString("GhiChu"));
        tx.setKieuXuLy(rs.getInt("KieuXuLy"));
        tx.setTenKH(rs.getString("TenKH"));
        tx.setTongTienHoaDon(rs.getBigDecimal("TongTien"));
        return tx;
    }

    private String emptyToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static final class ReturnStockInfo {
        private final int soldQty;
        private final int returnedQty;
        private final int currentStock;
        private final BigDecimal unitPrice;

        private ReturnStockInfo(int soldQty, int returnedQty, int currentStock, BigDecimal unitPrice) {
            this.soldQty = soldQty;
            this.returnedQty = returnedQty;
            this.currentStock = currentStock;
            this.unitPrice = unitPrice;
        }

        public int getSoldQty() {
            return soldQty;
        }

        public int getReturnedQty() {
            return returnedQty;
        }

        public int getCurrentStock() {
            return currentStock;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }
    }
}