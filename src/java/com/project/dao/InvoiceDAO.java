package com.project.dao;

import com.project.model.Invoice;
import com.project.model.InvoiceItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InvoiceDAO {

    public static class NewInvoiceItem {
        private final int maSach;
        private final int soLuong;

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

    public List<Invoice> findAll() {
        String sql = "SELECT hd.MaHD, hd.MaNV, hd.MaKH, hd.NgayLap, hd.TongTien, hd.GiamGia, hd.ThueVAT, kh.TenKH "
                + "FROM dbo.HoaDon hd "
                + "LEFT JOIN dbo.KhachHang kh ON hd.MaKH = kh.MaKH "
                + "ORDER BY hd.MaHD DESC";

        EntityManager em = JpaSupport.createEntityManager();
        try {
            List<Object[]> rows = em.createNativeQuery(sql).getResultList();
            List<Invoice> invoices = new ArrayList<Invoice>(rows.size());
            for (Object[] row : rows) {
                invoices.add(mapInvoiceRow(row));
            }
            return invoices;
        } finally {
            em.close();
        }
    }

    public Invoice findById(int maHD) {
        String sql = "SELECT hd.MaHD, hd.MaNV, hd.MaKH, hd.NgayLap, hd.TongTien, hd.GiamGia, hd.ThueVAT, kh.TenKH "
                + "FROM dbo.HoaDon hd "
                + "LEFT JOIN dbo.KhachHang kh ON hd.MaKH = kh.MaKH "
                + "WHERE hd.MaHD = ?";

        EntityManager em = JpaSupport.createEntityManager();
        try {
            List<Object[]> rows = em.createNativeQuery(sql)
                    .setParameter(1, maHD)
                    .getResultList();
            return rows.isEmpty() ? null : mapInvoiceRow(rows.get(0));
        } finally {
            em.close();
        }
    }

    public Invoice findByIdForCustomerLookup(int maHD, String phone, String email) {
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

        EntityManager em = JpaSupport.createEntityManager();
        try {
            var query = em.createNativeQuery(sql.toString());
            query.setParameter(1, maHD);
            if (hasPhone && hasEmail) {
                query.setParameter(2, phone.trim());
                query.setParameter(3, email.trim());
            } else if (hasPhone) {
                query.setParameter(2, phone.trim());
            } else {
                query.setParameter(2, email.trim());
            }

            List<Object[]> rows = query.getResultList();
            return rows.isEmpty() ? null : mapInvoiceRow(rows.get(0));
        } finally {
            em.close();
        }
    }

    public List<Invoice> findByIds(List<Integer> invoiceIds) {
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

        EntityManager em = JpaSupport.createEntityManager();
        try {
            var query = em.createNativeQuery(sql.toString());
            int idx = 1;
            for (Integer id : invoiceIds) {
                query.setParameter(idx++, id == null ? -1 : id);
            }

            List<Object[]> rows = query.getResultList();
            Map<Integer, Invoice> byId = new LinkedHashMap<Integer, Invoice>();
            for (Object[] row : rows) {
                Invoice invoice = mapInvoiceRow(row);
                byId.put(invoice.getMaHD(), invoice);
            }

            List<Invoice> ordered = new ArrayList<Invoice>();
            for (Integer id : invoiceIds) {
                if (id != null && byId.containsKey(id)) {
                    ordered.add(byId.get(id));
                }
            }
            return ordered;
        } finally {
            em.close();
        }
    }

    public List<InvoiceItem> findItemsByInvoiceId(int maHD) {
        List<InvoiceItem> items = new ArrayList<InvoiceItem>();
        String sql = "SELECT ct.MaCT, ct.MaHD, ct.MaSach, ct.SoLuong, ct.DonGia, ct.ThanhTien, s.TenSach "
                + "FROM dbo.ChiTietHoaDon ct "
                + "LEFT JOIN dbo.Sach s ON ct.MaSach = s.MaSach "
                + "WHERE ct.MaHD = ? ORDER BY ct.MaCT";

        EntityManager em = JpaSupport.createEntityManager();
        try {
            List<Object[]> rows = em.createNativeQuery(sql)
                    .setParameter(1, maHD)
                    .getResultList();
            for (Object[] row : rows) {
                items.add(mapInvoiceItemRow(row));
            }
            return items;
        } finally {
            em.close();
        }
    }

    public int createInvoice(Integer maKH, String maNV, BigDecimal giamGia, BigDecimal thueVat, List<NewInvoiceItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Hoa don phai co it nhat 1 dong chi tiet.");
        }

        EntityManager em = JpaSupport.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            int maHD = insertInvoiceHeader(em, maKH, maNV, giamGia, thueVat);
            BigDecimal subTotal = BigDecimal.ZERO;

            for (NewInvoiceItem item : items) {
                if (item.getSoLuong() <= 0) {
                    throw new IllegalArgumentException("So luong phai lon hon 0.");
                }

                StockInfo stock = getStockInfoForUpdate(em, item.getMaSach());
                if (stock == null) {
                    throw new IllegalArgumentException("Khong tim thay sach ma " + item.getMaSach());
                }

                if (stock.getSoLuongTon() < item.getSoLuong()) {
                    throw new IllegalArgumentException("So luong ton khong du cho sach ma " + item.getMaSach());
                }

                BigDecimal thanhTien = stock.getDonGia().multiply(BigDecimal.valueOf(item.getSoLuong()));
                insertInvoiceItem(em, maHD, item, stock.getDonGia(), thanhTien);
                updateBookStock(em, item.getMaSach(), stock.getSoLuongTon() - item.getSoLuong());
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

            updateInvoiceTotals(em, maHD, total, discount, vat);
            tx.commit();
            return maHD;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    private int insertInvoiceHeader(EntityManager em, Integer maKH, String maNV, BigDecimal giamGia, BigDecimal thueVat) {
        String sql = "INSERT INTO dbo.HoaDon (MaNV, MaKH, NgayLap, TongTien, GiamGia, ThueVAT) "
                + "OUTPUT INSERTED.MaHD VALUES (?, ?, GETDATE(), ?, ?, ?)";

        Number generated = (Number) em.createNativeQuery(sql)
                .setParameter(1, maNV == null || maNV.trim().isEmpty() ? null : maNV.trim())
                .setParameter(2, maKH)
                .setParameter(3, BigDecimal.ZERO)
                .setParameter(4, giamGia == null ? BigDecimal.ZERO : giamGia)
                .setParameter(5, thueVat == null ? BigDecimal.ZERO : thueVat)
                .getSingleResult();

        if (generated == null) {
            throw new IllegalArgumentException("Khong tao duoc hoa don.");
        }
        return generated.intValue();
    }

    private void updateInvoiceTotals(EntityManager em, int maHD, BigDecimal tongTien, BigDecimal giamGia, BigDecimal thueVat) {
        String sql = "UPDATE dbo.HoaDon SET TongTien = ?, GiamGia = ?, ThueVAT = ? WHERE MaHD = ?";
        em.createNativeQuery(sql)
                .setParameter(1, tongTien)
                .setParameter(2, giamGia)
                .setParameter(3, thueVat)
                .setParameter(4, maHD)
                .executeUpdate();
    }

    private StockInfo getStockInfoForUpdate(EntityManager em, int maSach) {
        String sql = "SELECT DonGia, SoLuong FROM dbo.Sach WITH (UPDLOCK, ROWLOCK) WHERE MaSach = ?";
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter(1, maSach)
                .getResultList();
        if (rows.isEmpty()) {
            return null;
        }

        Object[] row = rows.get(0);
        BigDecimal donGia = row[0] == null ? BigDecimal.ZERO : (BigDecimal) row[0];
        int soLuong = row[1] == null ? 0 : ((Number) row[1]).intValue();
        return new StockInfo(donGia, soLuong);
    }

    private void insertInvoiceItem(EntityManager em, int maHD, NewInvoiceItem item, BigDecimal donGia, BigDecimal thanhTien) {
        String sql = "INSERT INTO dbo.ChiTietHoaDon (MaHD, MaSach, SoLuong, DonGia, ThanhTien) VALUES (?, ?, ?, ?, ?)";
        em.createNativeQuery(sql)
                .setParameter(1, maHD)
                .setParameter(2, item.getMaSach())
                .setParameter(3, item.getSoLuong())
                .setParameter(4, donGia)
                .setParameter(5, thanhTien)
                .executeUpdate();
    }

    private void updateBookStock(EntityManager em, int maSach, int newStock) {
        String sql = "UPDATE dbo.Sach SET SoLuong = ? WHERE MaSach = ?";
        em.createNativeQuery(sql)
                .setParameter(1, newStock)
                .setParameter(2, maSach)
                .executeUpdate();
    }

    private Invoice mapInvoiceRow(Object[] row) {
        Invoice invoice = new Invoice();
        invoice.setMaHD(toInt(row[0]));
        invoice.setMaNV(toString(row[1]));
        invoice.setMaKH(row[2] == null ? null : toInt(row[2]));
        invoice.setNgayLap(row[3] == null ? null : (java.sql.Timestamp) row[3]);
        invoice.setTongTien(row[4] == null ? BigDecimal.ZERO : (BigDecimal) row[4]);
        invoice.setGiamGia(row[5] == null ? BigDecimal.ZERO : (BigDecimal) row[5]);
        invoice.setThueVAT(row[6] == null ? BigDecimal.ZERO : (BigDecimal) row[6]);
        invoice.setTenKH(toString(row[7]));

        if (row.length > 8) {
            invoice.setDienThoaiKH(toString(row[8]));
        }
        if (row.length > 9) {
            invoice.setEmailKH(toString(row[9]));
        }
        if (row.length > 10) {
            invoice.setDiaChiKH(toString(row[10]));
        }

        return invoice;
    }

    private InvoiceItem mapInvoiceItemRow(Object[] row) {
        InvoiceItem item = new InvoiceItem();
        item.setMaCT(toInt(row[0]));
        item.setMaHD(toInt(row[1]));
        item.setMaSach(toInt(row[2]));
        item.setSoLuong(toInt(row[3]));
        item.setDonGia(row[4] == null ? BigDecimal.ZERO : (BigDecimal) row[4]);
        item.setThanhTien(row[5] == null ? BigDecimal.ZERO : (BigDecimal) row[5]);
        item.setTenSach(toString(row[6]));
        return item;
    }

    private int toInt(Object value) {
        return value == null ? 0 : ((Number) value).intValue();
    }

    private String toString(Object value) {
        return value == null ? null : String.valueOf(value);
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
