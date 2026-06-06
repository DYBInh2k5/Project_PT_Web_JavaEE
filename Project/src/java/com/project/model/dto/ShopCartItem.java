package com.project.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class ShopCartItem implements Serializable {

    private Integer maSach;
    private String tenSach;
    private String tacGia;
    private BigDecimal donGia;
    private Integer soLuong;
    private Integer tonKho;
    private String anhBia;

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

    public String getTacGia() {
        return tacGia;
    }

    public void setTacGia(String tacGia) {
        this.tacGia = tacGia;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public Integer getTonKho() {
        return tonKho;
    }

    public void setTonKho(Integer tonKho) {
        this.tonKho = tonKho;
    }

    public String getAnhBia() {
        return anhBia;
    }

    public void setAnhBia(String anhBia) {
        this.anhBia = anhBia;
    }

    public BigDecimal getThanhTien() {
        BigDecimal gia = donGia == null ? BigDecimal.ZERO : donGia;
        int qty = soLuong == null ? 0 : soLuong;
        return gia.multiply(BigDecimal.valueOf(qty));
    }
}