// ===== DAO Thống kê Dashboard (DashboardDAO) — lấy số liệu tổng quan =====
// Đếm số lượng sách, khách hàng, hóa đơn và tổng doanh thu
package com.project.dao;

import com.project.model.dto.DashboardStats;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;

public class DashboardDAO {

    public DashboardStats loadStats() {
        DashboardStats stats = new DashboardStats();
        EntityManager em = JpaSupport.createEntityManager();
        try {
            stats.setTotalBooks(queryInt(em, "SELECT COUNT(*) FROM dbo.Sach"));
            stats.setTotalCustomers(queryInt(em, "SELECT COUNT(*) FROM dbo.KhachHang"));
            stats.setTotalInvoices(queryInt(em, "SELECT COUNT(*) FROM dbo.HoaDon"));
            stats.setRevenue(queryDecimal(em, "SELECT ISNULL(SUM(TongTien), 0) FROM dbo.HoaDon"));
            return stats;
        } finally {
            em.close();
        }
    }

    private int queryInt(EntityManager em, String sql) {
        Number value = (Number) em.createNativeQuery(sql).getSingleResult();
        return value == null ? 0 : value.intValue();
    }

    private BigDecimal queryDecimal(EntityManager em, String sql) {
        Object value = em.createNativeQuery(sql).getSingleResult();
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        return new BigDecimal(String.valueOf(value));
    }
}
