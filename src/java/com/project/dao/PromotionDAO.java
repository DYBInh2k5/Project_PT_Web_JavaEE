package com.project.dao;

import com.project.model.Promotion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PromotionDAO {

    public List<Promotion> findAll(String keyword) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT MaKM, TenKM, HinhThuc, GiaTri, MaCoupon, NgayBD, NgayKT ");
        sql.append("FROM dbo.KhuyenMai ");

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        if (hasKeyword) {
            sql.append("WHERE TenKM LIKE ? OR HinhThuc LIKE ? OR MaCoupon LIKE ? ");
        }

        sql.append("ORDER BY MaKM DESC");

        EntityManager em = JpaSupport.createEntityManager();
        try {
            var query = em.createNativeQuery(sql.toString());
            if (hasKeyword) {
                String q = "%" + keyword.trim() + "%";
                query.setParameter(1, q);
                query.setParameter(2, q);
                query.setParameter(3, q);
            }

            List<Object[]> rows = query.getResultList();
            List<Promotion> promotions = new ArrayList<Promotion>(rows.size());
            for (Object[] row : rows) {
                promotions.add(mapRow(row));
            }
            return promotions;
        } finally {
            em.close();
        }
    }

    public List<Promotion> findActivePromotions() {
        String sql = "SELECT MaKM, TenKM, HinhThuc, GiaTri, MaCoupon, NgayBD, NgayKT "
                + "FROM dbo.KhuyenMai "
                + "WHERE (NgayBD IS NULL OR NgayBD <= CAST(GETDATE() AS date)) "
                + "  AND (NgayKT IS NULL OR NgayKT >= CAST(GETDATE() AS date)) "
                + "ORDER BY MaKM DESC";

        EntityManager em = JpaSupport.createEntityManager();
        try {
            List<Object[]> rows = em.createNativeQuery(sql).getResultList();
            List<Promotion> promotions = new ArrayList<Promotion>(rows.size());
            for (Object[] row : rows) {
                promotions.add(mapRow(row));
            }
            return promotions;
        } finally {
            em.close();
        }
    }

    public Promotion findByCode(String couponCode) {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT MaKM, TenKM, HinhThuc, GiaTri, MaCoupon, NgayBD, NgayKT "
                + "FROM dbo.KhuyenMai "
                + "WHERE MaCoupon = ? "
                + "  AND (NgayBD IS NULL OR NgayBD <= CAST(GETDATE() AS date)) "
                + "  AND (NgayKT IS NULL OR NgayKT >= CAST(GETDATE() AS date))";

        EntityManager em = JpaSupport.createEntityManager();
        try {
            List<Object[]> rows = em.createNativeQuery(sql)
                    .setParameter(1, couponCode.trim())
                    .getResultList();
            return rows.isEmpty() ? null : mapRow(rows.get(0));
        } finally {
            em.close();
        }
    }

    public Promotion findById(int maKM) {
        String sql = "SELECT MaKM, TenKM, HinhThuc, GiaTri, MaCoupon, NgayBD, NgayKT "
                + "FROM dbo.KhuyenMai WHERE MaKM = ?";

        EntityManager em = JpaSupport.createEntityManager();
        try {
            List<Object[]> rows = em.createNativeQuery(sql)
                    .setParameter(1, maKM)
                    .getResultList();
            return rows.isEmpty() ? null : mapRow(rows.get(0));
        } finally {
            em.close();
        }
    }

    public void insert(Promotion promotion) {
        String sql = "INSERT INTO dbo.KhuyenMai (TenKM, HinhThuc, GiaTri, MaCoupon, NgayBD, NgayKT) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        EntityManager em = JpaSupport.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery(sql)
                    .setParameter(1, emptyToNull(promotion.getTenKM()))
                    .setParameter(2, emptyToNull(promotion.getHinhThuc()))
                    .setParameter(3, promotion.getGiaTri())
                    .setParameter(4, emptyToNull(promotion.getMaCoupon()))
                    .setParameter(5, promotion.getNgayBD())
                    .setParameter(6, promotion.getNgayKT())
                    .executeUpdate();
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public void update(Promotion promotion) {
        String sql = "UPDATE dbo.KhuyenMai "
                + "SET TenKM = ?, HinhThuc = ?, GiaTri = ?, MaCoupon = ?, NgayBD = ?, NgayKT = ? "
                + "WHERE MaKM = ?";

        EntityManager em = JpaSupport.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery(sql)
                    .setParameter(1, emptyToNull(promotion.getTenKM()))
                    .setParameter(2, emptyToNull(promotion.getHinhThuc()))
                    .setParameter(3, promotion.getGiaTri())
                    .setParameter(4, emptyToNull(promotion.getMaCoupon()))
                    .setParameter(5, promotion.getNgayBD())
                    .setParameter(6, promotion.getNgayKT())
                    .setParameter(7, promotion.getMaKM())
                    .executeUpdate();
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public void delete(int maKM) {
        String sql = "DELETE FROM dbo.KhuyenMai WHERE MaKM = ?";

        EntityManager em = JpaSupport.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery(sql)
                    .setParameter(1, maKM)
                    .executeUpdate();
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public boolean existsCouponCode(String couponCode, Integer excludeMaKM) {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(1) AS Cnt FROM dbo.KhuyenMai WHERE MaCoupon = ?"
                + (excludeMaKM == null ? "" : " AND MaKM <> ?");

        EntityManager em = JpaSupport.createEntityManager();
        try {
            var query = em.createNativeQuery(sql)
                    .setParameter(1, couponCode.trim());
            if (excludeMaKM != null) {
                query.setParameter(2, excludeMaKM);
            }

            Number count = (Number) query.getSingleResult();
            return count != null && count.intValue() > 0;
        } finally {
            em.close();
        }
    }

    public BigDecimal calculateDiscount(Promotion promotion, BigDecimal subtotal) {
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

        throw new IllegalArgumentException("Khuyen mai nay khong ho tro ap dung vao hoa don.");
    }

    private Promotion mapRow(Object[] row) {
        Promotion promotion = new Promotion();
        promotion.setMaKM(toInt(row[0]));
        promotion.setTenKM(toString(row[1]));
        promotion.setHinhThuc(toString(row[2]));
        promotion.setGiaTri(row[3] == null ? null : (BigDecimal) row[3]);
        promotion.setMaCoupon(toString(row[4]));
        promotion.setNgayBD(row[5] == null ? null : (java.sql.Date) row[5]);
        promotion.setNgayKT(row[6] == null ? null : (java.sql.Date) row[6]);
        return promotion;
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
}