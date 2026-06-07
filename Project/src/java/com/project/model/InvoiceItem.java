// ===== Model Chi tiết hóa đơn (InvoiceItem) — từng dòng sản phẩm trong đơn =====
// Mỗi InvoiceItem là một sản phẩm được mua trong hóa đơn
package com.project.model;

import java.math.BigDecimal;

public class InvoiceItem {

    // ===== Các thuộc tính chi tiết =====
    private Integer maCT;       // Mã chi tiết (PK, tự động tăng)
    private Integer maHD;       // Mã hóa đơn (FK → Invoice.maHD)
    private Integer maSach;     // Mã sách (FK → Book.maSach)
    private Integer soLuong;    // Số lượng mua
    private BigDecimal donGia;  // Đơn giá tại thời điểm mua
    private BigDecimal thanhTien; // Thành tiền = số lượng * đơn giá
    private String tenSach;     // Tên sách (lưu để tra cứu nhanh)

    public Integer getMaCT() {
        return maCT;
    }

    public void setMaCT(Integer maCT) {
        this.maCT = maCT;
    }

    public Integer getMaHD() {
        return maHD;
    }

    public void setMaHD(Integer maHD) {
        this.maHD = maHD;
    }

    public Integer getMaSach() {
        return maSach;
    }

    public void setMaSach(Integer maSach) {
        this.maSach = maSach;
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

    public String getTenSach() {
        return tenSach;
    }

    public void setTenSach(String tenSach) {
        this.tenSach = tenSach;
    }
}
