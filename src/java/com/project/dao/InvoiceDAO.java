package com.project.dao;

import com.project.db.SqlServerConnection;
import com.project.model.Invoice;
import com.project.model.InvoiceItem;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InvoiceDAO {

    public static class NewInvoiceItem {
        private int maSach;
        private int soLuong;

        public NewInvoiceItem(int maSach, int soLuong) {
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

    public List<Invoice> findAll() throws SQLException {
        List<Invoice> invoices = new ArrayList<Invoice>();
        String sql = "SELECT hd.MaHD, hd.MaNV, hd.MaKH, hd.NgayLap, hd.TongTien, hd.GiamGia, hd.ThueVAT, kh.TenKH "
                + "FROM dbo.HoaDon hd "
                + "LEFT JOIN dbo.KhachHang kh ON hd.MaKH = kh.MaKH "
                + "ORDER BY hd.MaHD DESC";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                invoices.add(mapInvoiceRow(rs));
            }
        }

        return invoices;
    }

    public Invoice findById(int maHD) throws SQLException {
        String sql = "SELECT hd.MaHD, hd.MaNV, hd.MaKH, hd.NgayLap, hd.TongTien, hd.GiamGia, hd.ThueVAT, kh.TenKH "
                + "FROM dbo.HoaDon hd "
                + "LEFT JOIN dbo.KhachHang kh ON hd.MaKH = kh.MaKH "
                + "WHERE hd.MaHD = ?";

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapInvoiceRow(rs);
                }
            }
        }

        return null;
    }

    public Invoice findByIdForCustomerLookup(int maHD, String phone, String email) throws SQLException {
        boolean hasPhone = phone != null && !phone.trim().isEmpty();
        boolean hasEmail = email != null && !email.trim().isEmpty();
        if (!hasPhone && !hasEmail) {
            return null;
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT hd.MaHD, hd.MaNV, hd.MaKH, hd.NgayLap, hd.TongTien, hd.GiamGia, hd.ThueVAT, ");
        sql.append("kh.TenKH, kh.DienThoai, kh.Email, kh.DiaChi ");
        sql.append("FROM dbo.HoaDon hd ");
        sql.append("LEFT JOIN dbo.KhachHang kh ON hd.MaKH = kh.MaKH ");
        sql.append("WHERE hd.MaHD = ? AND hd.MaKH IS NOT NULL AND (");
        if (hasPhone && hasEmail) {
            sql.append("kh.DienThoai = ? OR kh.Email = ?");
        } else if (hasPhone) {
            sql.append("kh.DienThoai = ?");
        } else {
            sql.append("kh.Email = ?");
        }
        sql.append(")");

        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setInt(1, maHD);
            int idx = 2;
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
                    return mapInvoiceRow(rs);
                }
            }
        }

        return null;
    }

    public List<Invoice> findByIds(List<Integer> invoiceIds) throws SQLException {
        if (invoiceIds == null || invoiceIds.isEmpty()) {
            return Collections.emptyList();
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT hd.MaHD, hd.MaNV, hd.MaKH, hd.NgayLap, hd.TongTien, hd.GiamGia, hd.ThueVAT, ");
        sql.append("kh.TenKH, kh.DienThoai, kh.Email, kh.DiaChi ");
        sql.append("FROM dbo.HoaDon hd ");
        sql.append("LEFT JOIN dbo.KhachHang kh ON hd.MaKH = kh.MaKH ");
        sql.append("WHERE hd.MaHD IN (");
        for (int i = 0; i < invoiceIds.size(); i++) {
            if (i > 0) {
                sql.append(",");
            }
            sql.append("?");
        }
        sql.append(")");

        List<Invoice> invoices = new ArrayList<Invoice>();
        try (Connection conn = SqlServerConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            for (Integer id : invoiceIds) {
                ps.setInt(idx++, id == null ? -1 : id);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapInvoiceRow(rs));
                }
            }
        }

        List<Invoice> ordered = new ArrayList<Invoice>();
        for (Integer id : invoiceIds) {
            if (id == null) {
                continue;
            }
            for (Invoice invoice : invoices) {
                if (id.equals(invoice.getMaHD())) {
                    ordered.add(invoice);
                    break;
                }
            }
        }

        return ordered;
    }

    public List<InvoiceItem> findItemsByInvoiceId(int maHD) throws SQLException {
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

    public int createInvoice(Integer maKH, String maNV, BigDecimal giamGia, BigDecimal thueVat, List<NewInvoiceItem> items)
            throws SQLException {

        if (items == null || items.isEmpty()) {
            throw new SQLException("Hoa don phai co it nhat 1 dong chi tiet.");
        }

        Connection conn = null;
        try {
            conn = SqlServerConnection.getConnection();
            conn.setAutoCommit(false);

            int maHD = insertInvoiceHeader(conn, maKH, maNV, giamGia, thueVat);
            BigDecimal subTotal = BigDecimal.ZERO;

            for (NewInvoiceItem item : items) {
                if (item.getSoLuong() <= 0) {
                    throw new SQLException("So luong phai lon hon 0.");
                }

                StockInfo stock = getStockInfoForUpdate(conn, item.getMaSach());
                if (stock == null) {
                    throw new SQLException("Khong tim thay sach ma " + item.getMaSach());
                }

                if (stock.getSoLuongTon() < item.getSoLuong()) {
                    throw new SQLException("So luong ton khong du cho sach ma " + item.getMaSach());
                }

                BigDecimal thanhTien = stock.getDonGia().multiply(BigDecimal.valueOf(item.getSoLuong()));
                insertInvoiceItem(conn, maHD, item, stock.getDonGia(), thanhTien);
                updateBookStock(conn, item.getMaSach(), stock.getSoLuongTon() - item.getSoLuong());
                subTotal = subTotal.add(thanhTien);
            }

            BigDecimal discount = giamGia == null ? BigDecimal.ZERO : giamGia;
            if (discount.compareTo(subTotal) > 0) {
                discount = subTotal;
            }
            BigDecimal vat = thueVat == null ? BigDecimal.ZERO : thueVat;
            BigDecimal total = subTotal.subtract(discount).add(vat);
            if (total.compareTo(BigDecimal.ZERO) < 0) {
                total = BigDecimal.ZERO;
            }

            updateInvoiceTotals(conn, maHD, total, discount, vat);
            conn.commit();
            return maHD;

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

    private int insertInvoiceHeader(Connection conn, Integer maKH, String maNV, BigDecimal giamGia, BigDecimal thueVat)
            throws SQLException {

        String sql = "INSERT INTO dbo.HoaDon (MaNV, MaKH, NgayLap, TongTien, GiamGia, ThueVAT) "
                + "OUTPUT INSERTED.MaHD VALUES (?, ?, GETDATE(), ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (maNV == null || maNV.trim().isEmpty()) {
                ps.setNull(1, Types.NVARCHAR);
            } else {
                ps.setString(1, maNV.trim());
            }

            if (maKH == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, maKH);
            }

            ps.setBigDecimal(3, BigDecimal.ZERO);
            ps.setBigDecimal(4, giamGia == null ? BigDecimal.ZERO : giamGia);
            ps.setBigDecimal(5, thueVat == null ? BigDecimal.ZERO : thueVat);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Khong tao duoc hoa don.");
    }

    private void updateInvoiceTotals(Connection conn, int maHD, BigDecimal tongTien, BigDecimal giamGia, BigDecimal thueVat)
            throws SQLException {

        String sql = "UPDATE dbo.HoaDon SET TongTien = ?, GiamGia = ?, ThueVAT = ? WHERE MaHD = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, tongTien);
            ps.setBigDecimal(2, giamGia);
            ps.setBigDecimal(3, thueVat);
            ps.setInt(4, maHD);
            ps.executeUpdate();
        }
    }

    private StockInfo getStockInfoForUpdate(Connection conn, int maSach) throws SQLException {
        String sql = "SELECT DonGia, SoLuong FROM dbo.Sach WITH (UPDLOCK, ROWLOCK) WHERE MaSach = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maSach);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal donGia = rs.getBigDecimal("DonGia");
                    if (donGia == null) {
                        donGia = BigDecimal.ZERO;
                    }

                    int soLuong = rs.getInt("SoLuong");
                    if (rs.wasNull()) {
                        soLuong = 0;
                    }

                    return new StockInfo(donGia, soLuong);
                }
            }
        }

        return null;
    }

    private void insertInvoiceItem(Connection conn, int maHD, NewInvoiceItem item, BigDecimal donGia, BigDecimal thanhTien)
            throws SQLException {

        String sql = "INSERT INTO dbo.ChiTietHoaDon (MaHD, MaSach, SoLuong, DonGia, ThanhTien) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maHD);
            ps.setInt(2, item.getMaSach());
            ps.setInt(3, item.getSoLuong());
            ps.setBigDecimal(4, donGia);
            ps.setBigDecimal(5, thanhTien);
            ps.executeUpdate();
        }
    }

    private void updateBookStock(Connection conn, int maSach, int newStock) throws SQLException {
        String sql = "UPDATE dbo.Sach SET SoLuong = ? WHERE MaSach = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newStock);
            ps.setInt(2, maSach);
            ps.executeUpdate();
        }
    }

    private Invoice mapInvoiceRow(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setMaHD(rs.getInt("MaHD"));
        invoice.setMaNV(rs.getString("MaNV"));

        int maKH = rs.getInt("MaKH");
        if (rs.wasNull()) {
            invoice.setMaKH(null);
        } else {
            invoice.setMaKH(maKH);
        }

        invoice.setNgayLap(rs.getTimestamp("NgayLap"));
        invoice.setTongTien(rs.getBigDecimal("TongTien"));
        invoice.setGiamGia(rs.getBigDecimal("GiamGia"));
        invoice.setThueVAT(rs.getBigDecimal("ThueVAT"));
        invoice.setTenKH(rs.getString("TenKH"));

        try {
            invoice.setDienThoaiKH(rs.getString("DienThoai"));
        } catch (SQLException ignore) {
        }
        try {
            invoice.setEmailKH(rs.getString("Email"));
        } catch (SQLException ignore) {
        }
        try {
            invoice.setDiaChiKH(rs.getString("DiaChi"));
        } catch (SQLException ignore) {
        }

        return invoice;
    }

    private static final class StockInfo {
        private final BigDecimal donGia;
        private final int soLuongTon;

        private StockInfo(BigDecimal donGia, int soLuongTon) {
            this.donGia = donGia;
            this.soLuongTon = soLuongTon;
        }

        public BigDecimal getDonGia() {
            return donGia;
        }

        public int getSoLuongTon() {
            return soLuongTon;
        }
    }
}
