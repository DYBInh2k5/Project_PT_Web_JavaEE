package com.project.dao;

import com.project.model.InvoiceItem;
import com.project.model.ReturnItem;
import com.project.model.ReturnTransaction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ReturnDAO {

    public static class NewReturnItem {
        private final int maSach;
        private final int soLuong;

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

    public List<ReturnTransaction> findAll() {
        String sql = "SELECT dt.MaDT, dt.MaHD, dt.NgayDoi, dt.LyDo, dt.GhiChu, dt.KieuXuLy, hd.TongTien, kh.TenKH "
                + "FROM dbo.DoiTra dt "
                + "LEFT JOIN dbo.HoaDon hd ON dt.MaHD = hd.MaHD "
                + "LEFT JOIN dbo.KhachHang kh ON hd.MaKH = kh.MaKH "
                + "ORDER BY dt.MaDT DESC";

        EntityManager em = JpaSupport.createEntityManager();
        try {
            List<Object[]> rows = em.createNativeQuery(sql).getResultList();
            List<ReturnTransaction> returns = new ArrayList<ReturnTransaction>(rows.size());
            for (Object[] row : rows) {
                returns.add(mapHeader(row));
            }
            return returns;
        } finally {
            em.close();
        }
    }

    public ReturnTransaction findById(int maDT) {
        String sql = "SELECT dt.MaDT, dt.MaHD, dt.NgayDoi, dt.LyDo, dt.GhiChu, dt.KieuXuLy, hd.TongTien, kh.TenKH "
                + "FROM dbo.DoiTra dt "
                + "LEFT JOIN dbo.HoaDon hd ON dt.MaHD = hd.MaHD "
                + "LEFT JOIN dbo.KhachHang kh ON hd.MaKH = kh.MaKH "
                + "WHERE dt.MaDT = ?";

        EntityManager em = JpaSupport.createEntityManager();
        try {
            List<Object[]> rows = em.createNativeQuery(sql)
                    .setParameter(1, maDT)
                    .getResultList();
            return rows.isEmpty() ? null : mapHeader(rows.get(0));
        } finally {
            em.close();
        }
    }

    public List<ReturnItem> findItemsByReturnId(int maDT) {
        String sql = "SELECT ct.MaCTDT, ct.MaDT, ct.MaSach, ct.SoLuong, ct.DonGia, ct.ThanhTien, s.TenSach "
                + "FROM dbo.ChiTietDoiTra ct "
                + "LEFT JOIN dbo.Sach s ON ct.MaSach = s.MaSach "
                + "WHERE ct.MaDT = ? ORDER BY ct.MaCTDT";

        EntityManager em = JpaSupport.createEntityManager();
        try {
            List<Object[]> rows = em.createNativeQuery(sql)
                    .setParameter(1, maDT)
                    .getResultList();
            List<ReturnItem> items = new ArrayList<ReturnItem>(rows.size());
            for (Object[] row : rows) {
                items.add(mapReturnItem(row));
            }
            return items;
        } finally {
            em.close();
        }
    }

    public List<InvoiceItem> findInvoiceItems(int maHD) {
        String sql = "SELECT ct.MaCT, ct.MaHD, ct.MaSach, ct.SoLuong, ct.DonGia, ct.ThanhTien, s.TenSach "
                + "FROM dbo.ChiTietHoaDon ct "
                + "LEFT JOIN dbo.Sach s ON ct.MaSach = s.MaSach "
                + "WHERE ct.MaHD = ? ORDER BY ct.MaCT";

        EntityManager em = JpaSupport.createEntityManager();
        try {
            List<Object[]> rows = em.createNativeQuery(sql)
                    .setParameter(1, maHD)
                    .getResultList();
            List<InvoiceItem> items = new ArrayList<InvoiceItem>(rows.size());
            for (Object[] row : rows) {
                items.add(mapInvoiceItem(row));
            }
            return items;
        } finally {
            em.close();
        }
    }

    public int createReturn(int maHD, String lyDo, String ghiChu, int kieuXuLy, List<NewReturnItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Phai co it nhat 1 dong chi tiet doi tra.");
        }

        EntityManager em = JpaSupport.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            int maDT = insertHeader(em, maHD, lyDo, ghiChu, kieuXuLy);
            for (NewReturnItem item : items) {
                if (item.getSoLuong() <= 0) {
                    throw new IllegalArgumentException("So luong doi tra phai lon hon 0.");
                }

                ReturnStockInfo stockInfo = getInvoiceStockInfoForUpdate(em, maHD, item.getMaSach());
                if (stockInfo == null) {
                    throw new IllegalArgumentException("Khong tim thay sach ma " + item.getMaSach() + " trong hoa don " + maHD);
                }

                int available = stockInfo.getSoldQty() - stockInfo.getReturnedQty();
                if (item.getSoLuong() > available) {
                    throw new IllegalArgumentException("So luong doi tra vuot qua so luong con co the doi tra cho sach ma " + item.getMaSach());
                }

                BigDecimal thanhTien = stockInfo.getUnitPrice().multiply(BigDecimal.valueOf(item.getSoLuong()));
                insertDetail(em, maDT, item.getMaSach(), item.getSoLuong(), stockInfo.getUnitPrice(), thanhTien);
                updateStock(em, item.getMaSach(), stockInfo.getCurrentStock() + item.getSoLuong());
            }

            tx.commit();
            return maDT;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    private int insertHeader(EntityManager em, int maHD, String lyDo, String ghiChu, int kieuXuLy) {
        String sql = "INSERT INTO dbo.DoiTra (MaHD, NgayDoi, LyDo, GhiChu, KieuXuLy) OUTPUT INSERTED.MaDT VALUES (?, GETDATE(), ?, ?, ?)";
        Number generated = (Number) em.createNativeQuery(sql)
                .setParameter(1, maHD)
                .setParameter(2, emptyToNull(lyDo))
                .setParameter(3, emptyToNull(ghiChu))
                .setParameter(4, kieuXuLy)
                .getSingleResult();

        if (generated == null) {
            throw new IllegalArgumentException("Khong tao duoc phieu doi tra.");
        }
        return generated.intValue();
    }

    private void insertDetail(EntityManager em, int maDT, int maSach, int soLuong, BigDecimal donGia, BigDecimal thanhTien) {
        String sql = "INSERT INTO dbo.ChiTietDoiTra (MaDT, MaSach, SoLuong, DonGia, ThanhTien) VALUES (?, ?, ?, ?, ?)";
        em.createNativeQuery(sql)
                .setParameter(1, maDT)
                .setParameter(2, maSach)
                .setParameter(3, soLuong)
                .setParameter(4, donGia)
                .setParameter(5, thanhTien)
                .executeUpdate();
    }

    private void updateStock(EntityManager em, int maSach, int newStock) {
        String sql = "UPDATE dbo.Sach SET SoLuong = ? WHERE MaSach = ?";
        em.createNativeQuery(sql)
                .setParameter(1, newStock)
                .setParameter(2, maSach)
                .executeUpdate();
    }

    private ReturnStockInfo getInvoiceStockInfoForUpdate(EntityManager em, int maHD, int maSach) {
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

        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter(1, maHD)
                .setParameter(2, maSach)
                .getResultList();
        if (rows.isEmpty()) {
            return null;
        }

        Object[] row = rows.get(0);
        int soldQty = toInt(row[0]);
        BigDecimal unitPrice = row[1] == null ? BigDecimal.ZERO : (BigDecimal) row[1];
        int currentStock = toInt(row[2]);
        int returnedQty = toInt(row[3]);
        return new ReturnStockInfo(soldQty, returnedQty, currentStock, unitPrice);
    }

    private ReturnTransaction mapHeader(Object[] row) {
        ReturnTransaction tx = new ReturnTransaction();
        tx.setMaDT(toInt(row[0]));
        tx.setMaHD(toInt(row[1]));
        tx.setNgayDoi(row[2] == null ? null : (java.sql.Timestamp) row[2]);
        tx.setLyDo(toString(row[3]));
        tx.setGhiChu(toString(row[4]));
        tx.setKieuXuLy(toInt(row[5]));
        tx.setTongTienHoaDon(row[6] == null ? BigDecimal.ZERO : (BigDecimal) row[6]);
        tx.setTenKH(toString(row[7]));
        return tx;
    }

    private ReturnItem mapReturnItem(Object[] row) {
        ReturnItem item = new ReturnItem();
        item.setMaCTDT(toInt(row[0]));
        item.setMaDT(toInt(row[1]));
        item.setMaSach(toInt(row[2]));
        item.setSoLuong(toInt(row[3]));
        item.setDonGia(row[4] == null ? BigDecimal.ZERO : (BigDecimal) row[4]);
        item.setThanhTien(row[5] == null ? BigDecimal.ZERO : (BigDecimal) row[5]);
        item.setTenSach(toString(row[6]));
        return item;
    }

    private InvoiceItem mapInvoiceItem(Object[] row) {
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