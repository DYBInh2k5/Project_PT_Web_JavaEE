package com.project.model.dto;

import java.math.BigDecimal;

/**
 * TopBookReportItem — DTO chứa thông tin báo cáo về một cuốn sách bán chạy.
 * Dùng cho thống kê top sách: tổng số lượng đã bán và tổng doanh thu.
 */
public class TopBookReportItem {

    /** Mã sách */
    private Integer maSach;
    /** Tên sách */
    private String tenSach;
    /** Tổng số lượng đã bán */
    private Integer tongSoLuong;
    /** Tổng doanh thu từ sách này (số lượng * đơn giá) */
    private BigDecimal tongDoanhThu;

    public Integer getMaSach() {
        return maSach;
    }

    public void setMaSach(Integer maSach) {
        this.maSach = maSach;
    }

    public String getTenSach() {
        return tenSach;
    }

    public void setTenSach(String tenSach) {
        this.tenSach = tenSach;
    }

    public Integer getTongSoLuong() {
        return tongSoLuong;
    }

    public void setTongSoLuong(Integer tongSoLuong) {
        this.tongSoLuong = tongSoLuong;
    }

    public BigDecimal getTongDoanhThu() {
        return tongDoanhThu;
    }

    public void setTongDoanhThu(BigDecimal tongDoanhThu) {
        this.tongDoanhThu = tongDoanhThu;
    }
}
