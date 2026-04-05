package com.project.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class PurchaseReceiptItem implements Serializable {

    private Integer maCTPN;
    private Integer maPN;
    private Integer maSach;
    private String tenSach;
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;

    public Integer getMaCTPN() {
        return maCTPN;
    }

    public void setMaCTPN(Integer maCTPN) {
        this.maCTPN = maCTPN;
    }

    public Integer getMaPN() {
        return maPN;
    }

    public void setMaPN(Integer maPN) {
        this.maPN = maPN;
    }

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

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public BigDecimal getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }
}