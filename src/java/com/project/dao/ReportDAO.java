package com.project.dao;

import com.project.model.dto.RevenueByDate;
import com.project.model.dto.TopBookReportItem;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    public List<RevenueByDate> findRevenueByDate(Date fromDate, Date toDate) {
        String sql = "SELECT CAST(hd.NgayLap AS date) AS Ngay, ISNULL(SUM(hd.TongTien), 0) AS DoanhThu "
                + "FROM dbo.HoaDon hd "
                + "WHERE (? IS NULL OR CAST(hd.NgayLap AS date) >= ?) "
                + "AND (? IS NULL OR CAST(hd.NgayLap AS date) <= ?) "
                + "GROUP BY CAST(hd.NgayLap AS date) "
                + "ORDER BY Ngay";

        EntityManager em = JpaSupport.createEntityManager();
        try {
            List<Object[]> rows = em.createNativeQuery(sql)
                    .setParameter(1, fromDate)
                    .setParameter(2, fromDate)
                    .setParameter(3, toDate)
                    .setParameter(4, toDate)
                    .getResultList();

            List<RevenueByDate> result = new ArrayList<RevenueByDate>(rows.size());
            for (Object[] row : rows) {
                RevenueByDate item = new RevenueByDate();
                item.setNgay(row[0] == null ? null : (Date) row[0]);
                item.setDoanhThu(row[1] == null ? BigDecimal.ZERO : (BigDecimal) row[1]);
                result.add(item);
            }
            return result;
        } finally {
            em.close();
        }
    }

    public List<TopBookReportItem> findTopBooks(Date fromDate, Date toDate, int topN) {
        String sql = "SELECT TOP (?) s.MaSach, s.TenSach, SUM(ISNULL(ct.SoLuong, 0)) AS TongSoLuong, "
                + "SUM(ISNULL(ct.ThanhTien, 0)) AS TongDoanhThu "
                + "FROM dbo.ChiTietHoaDon ct "
                + "INNER JOIN dbo.HoaDon hd ON ct.MaHD = hd.MaHD "
                + "INNER JOIN dbo.Sach s ON ct.MaSach = s.MaSach "
                + "WHERE (? IS NULL OR CAST(hd.NgayLap AS date) >= ?) "
                + "AND (? IS NULL OR CAST(hd.NgayLap AS date) <= ?) "
                + "GROUP BY s.MaSach, s.TenSach "
                + "ORDER BY TongSoLuong DESC, TongDoanhThu DESC";

        EntityManager em = JpaSupport.createEntityManager();
        try {
            List<Object[]> rows = em.createNativeQuery(sql)
                    .setParameter(1, topN)
                    .setParameter(2, fromDate)
                    .setParameter(3, fromDate)
                    .setParameter(4, toDate)
                    .setParameter(5, toDate)
                    .getResultList();

            List<TopBookReportItem> result = new ArrayList<TopBookReportItem>(rows.size());
            for (Object[] row : rows) {
                TopBookReportItem item = new TopBookReportItem();
                item.setMaSach(row[0] == null ? 0 : ((Number) row[0]).intValue());
                item.setTenSach(row[1] == null ? null : String.valueOf(row[1]));
                item.setTongSoLuong(row[2] == null ? 0 : ((Number) row[2]).intValue());
                item.setTongDoanhThu(row[3] == null ? BigDecimal.ZERO : (BigDecimal) row[3]);
                result.add(item);
            }
            return result;
        } finally {
            em.close();
        }
    }
}
