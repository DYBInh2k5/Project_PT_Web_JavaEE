package com.project.dao;

import com.project.model.PurchaseReceipt;
import com.project.model.PurchaseReceiptItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDAO {

    public static class NewPurchaseItem {
        private final int maSach;
        private final int soLuong;
        private final BigDecimal donGia;

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

    public List<PurchaseReceipt> findAll() {
        String sql = "SELECT MaPN, NgayNhap, MaNV, TongTien FROM dbo.PhieuNhap ORDER BY MaPN DESC";

        EntityManager em = JpaSupport.createEntityManager();
        try {
            List<Object[]> rows = em.createNativeQuery(sql).getResultList();
            List<PurchaseReceipt> receipts = new ArrayList<PurchaseReceipt>(rows.size());
            for (Object[] row : rows) {
                receipts.add(mapReceipt(row));
            }
            return receipts;
        } finally {
            em.close();
        }
    }

    public PurchaseReceipt findById(int maPN) {
        String sql = "SELECT MaPN, NgayNhap, MaNV, TongTien FROM dbo.PhieuNhap WHERE MaPN = ?";

        EntityManager em = JpaSupport.createEntityManager();
        try {
            List<Object[]> rows = em.createNativeQuery(sql)
                    .setParameter(1, maPN)
                    .getResultList();
            return rows.isEmpty() ? null : mapReceipt(rows.get(0));
        } finally {
            em.close();
        }
    }

    public List<PurchaseReceiptItem> findItemsByPurchaseId(int maPN) {
        String sql = "SELECT ct.MaCTPN, ct.MaPN, ct.MaSach, ct.SoLuong, ct.DonGia, ct.ThanhTien, s.TenSach "
                + "FROM dbo.ChiTietPhieuNhap ct "
                + "LEFT JOIN dbo.Sach s ON ct.MaSach = s.MaSach "
                + "WHERE ct.MaPN = ? ORDER BY ct.MaCTPN";

        EntityManager em = JpaSupport.createEntityManager();
        try {
            List<Object[]> rows = em.createNativeQuery(sql)
                    .setParameter(1, maPN)
                    .getResultList();
            List<PurchaseReceiptItem> items = new ArrayList<PurchaseReceiptItem>(rows.size());
            for (Object[] row : rows) {
                items.add(mapItem(row));
            }
            return items;
        } finally {
            em.close();
        }
    }

    public int createPurchase(Integer maNV, List<NewPurchaseItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Phieu nhap phai co it nhat 1 dong chi tiet.");
        }

        EntityManager em = JpaSupport.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            int maPN = insertHeader(em, maNV);
            BigDecimal total = BigDecimal.ZERO;

            for (NewPurchaseItem item : items) {
                if (item.getSoLuong() <= 0) {
                    throw new IllegalArgumentException("So luong phai lon hon 0.");
                }
                if (item.getDonGia() == null || item.getDonGia().compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Don gia khong hop le cho sach ma " + item.getMaSach());
                }

                BigDecimal thanhTien = item.getDonGia().multiply(BigDecimal.valueOf(item.getSoLuong()));
                insertDetail(em, maPN, item.getMaSach(), item.getSoLuong(), item.getDonGia(), thanhTien);
                increaseStock(em, item.getMaSach(), item.getSoLuong());
                total = total.add(thanhTien);
            }

            updateTotal(em, maPN, total);
            tx.commit();
            return maPN;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    private int insertHeader(EntityManager em, Integer maNV) {
        String sql = "INSERT INTO dbo.PhieuNhap (NgayNhap, MaNV, TongTien) OUTPUT INSERTED.MaPN VALUES (GETDATE(), ?, 0)";
        Number generated = (Number) em.createNativeQuery(sql)
                .setParameter(1, maNV)
                .getSingleResult();
        if (generated == null) {
            throw new IllegalArgumentException("Khong tao duoc phieu nhap.");
        }
        return generated.intValue();
    }

    private void insertDetail(EntityManager em, int maPN, int maSach, int soLuong, BigDecimal donGia, BigDecimal thanhTien) {
        String sql = "INSERT INTO dbo.ChiTietPhieuNhap (MaPN, MaSach, SoLuong, DonGia, ThanhTien) VALUES (?, ?, ?, ?, ?)";
        em.createNativeQuery(sql)
                .setParameter(1, maPN)
                .setParameter(2, maSach)
                .setParameter(3, soLuong)
                .setParameter(4, donGia)
                .setParameter(5, thanhTien)
                .executeUpdate();
    }

    private void increaseStock(EntityManager em, int maSach, int delta) {
        String sql = "UPDATE dbo.Sach SET SoLuong = ISNULL(SoLuong, 0) + ? WHERE MaSach = ?";
        em.createNativeQuery(sql)
                .setParameter(1, delta)
                .setParameter(2, maSach)
                .executeUpdate();
    }

    private void updateTotal(EntityManager em, int maPN, BigDecimal total) {
        String sql = "UPDATE dbo.PhieuNhap SET TongTien = ? WHERE MaPN = ?";
        em.createNativeQuery(sql)
                .setParameter(1, total)
                .setParameter(2, maPN)
                .executeUpdate();
    }

    private PurchaseReceipt mapReceipt(Object[] row) {
        PurchaseReceipt receipt = new PurchaseReceipt();
        receipt.setMaPN(toInt(row[0]));
        receipt.setNgayNhap(row[1] == null ? null : (java.sql.Timestamp) row[1]);
        receipt.setMaNV(row[2] == null ? null : toInt(row[2]));
        receipt.setTongTien(row[3] == null ? BigDecimal.ZERO : (BigDecimal) row[3]);
        return receipt;
    }

    private PurchaseReceiptItem mapItem(Object[] row) {
        PurchaseReceiptItem item = new PurchaseReceiptItem();
        item.setMaCTPN(toInt(row[0]));
        item.setMaPN(toInt(row[1]));
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
}