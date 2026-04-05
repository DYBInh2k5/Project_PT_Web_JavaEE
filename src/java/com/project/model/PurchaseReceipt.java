package com.project.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class PurchaseReceipt implements Serializable {

    private Integer maPN;
    private Timestamp ngayNhap;
    private Integer maNV;
    private BigDecimal tongTien;

    public Integer getMaPN() {
        return maPN;
    }

    public void setMaPN(Integer maPN) {
        this.maPN = maPN;
    }

    public Timestamp getNgayNhap() {
        return ngayNhap;
    }

    public void setNgayNhap(Timestamp ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    public Integer getMaNV() {
        return maNV;
    }

    public void setMaNV(Integer maNV) {
        this.maNV = maNV;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }
}