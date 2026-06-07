// ===== Model Hóa đơn (Invoice) — lưu thông tin đơn hàng =====
// Mỗi hóa đơn bao gồm thông tin khách hàng, tổng tiền, giảm giá, thuế
package com.project.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Invoice {

    // ===== Các thuộc tính của hóa đơn =====
    private Integer maHD;          // Mã hóa đơn (PK, tự động tăng)
    private String maNV;           // Mã nhân viên xử lý (nếu có)
    private Integer maKH;          // Mã khách hàng
    private Timestamp ngayLap;     // Ngày lập hóa đơn (datetime)
    private BigDecimal tongTien;   // Tổng tiền trước giảm giá
    private BigDecimal giamGia;    // Số tiền giảm giá
    private BigDecimal thueVAT;    // Thuế VAT
    private String trangThai;      // Trạng thái: "Chờ xử lý", "Đã xác nhận", "Đã hủy"
    // Thông tin khách hàng (lưu trực tiếp để tra cứu nhanh, không cần JOIN)
    private String tenKH;          // Tên khách hàng
    private String dienThoaiKH;    // Số điện thoại khách hàng
    private String emailKH;        // Email khách hàng
    private String diaChiKH;       // Địa chỉ khách hàng

    public Integer getMaHD() {
        return maHD;
    }

    public void setMaHD(Integer maHD) {
        this.maHD = maHD;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public Integer getMaKH() {
        return maKH;
    }

    public void setMaKH(Integer maKH) {
        this.maKH = maKH;
    }

    public Timestamp getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(Timestamp ngayLap) {
        this.ngayLap = ngayLap;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public BigDecimal getGiamGia() {
        return giamGia;
    }

    public void setGiamGia(BigDecimal giamGia) {
        this.giamGia = giamGia;
    }

    public BigDecimal getThueVAT() {
        return thueVAT;
    }

    public void setThueVAT(BigDecimal thueVAT) {
        this.thueVAT = thueVAT;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getTenKH() {
        return tenKH;
    }

    public void setTenKH(String tenKH) {
        this.tenKH = tenKH;
    }

    public String getDienThoaiKH() {
        return dienThoaiKH;
    }

    public void setDienThoaiKH(String dienThoaiKH) {
        this.dienThoaiKH = dienThoaiKH;
    }

    public String getEmailKH() {
        return emailKH;
    }

    public void setEmailKH(String emailKH) {
        this.emailKH = emailKH;
    }

    public String getDiaChiKH() {
        return diaChiKH;
    }

    public void setDiaChiKH(String diaChiKH) {
        this.diaChiKH = diaChiKH;
    }
}
