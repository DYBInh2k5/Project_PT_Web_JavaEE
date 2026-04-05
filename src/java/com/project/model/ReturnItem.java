package com.project.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class ReturnItem implements Serializable {

    private int maCTDT;
    private int maDT;
    private int maSach;
    private String tenSach;
    private int soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;

    public int getMaCTDT() {
        return maCTDT;
    }

    public void setMaCTDT(int maCTDT) {
        this.maCTDT = maCTDT;
    }

    public int getMaDT() {
        return maDT;
    }

    public void setMaDT(int maDT) {
        this.maDT = maDT;
    }

    public int getMaSach() {
        return maSach;
    }

    public void setMaSach(int maSach) {
        this.maSach = maSach;
    }

    public String getTenSach() {
        return tenSach;
    }

    public void setTenSach(String tenSach) {
        this.tenSach = tenSach;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
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