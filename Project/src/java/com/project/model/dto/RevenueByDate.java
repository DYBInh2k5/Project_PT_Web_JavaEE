package com.project.model.dto;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * RevenueByDate — DTO lưu doanh thu theo từng ngày.
 * Dùng cho biểu đồ doanh thu 7 ngày và các báo cáo thống kê theo ngày.
 */
public class RevenueByDate {

    /** Ngày thống kê */
    private Date ngay;
    /** Doanh thu trong ngày đó */
    private BigDecimal doanhThu;

    public Date getNgay() {
        return ngay;
    }

    public void setNgay(Date ngay) {
        this.ngay = ngay;
    }

    public BigDecimal getDoanhThu() {
        return doanhThu;
    }

    public void setDoanhThu(BigDecimal doanhThu) {
        this.doanhThu = doanhThu;
    }
}
