package com.project.model.dto;

import java.math.BigDecimal;
import java.sql.Date;

public class RevenueByDate {

    private Date ngay;
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
