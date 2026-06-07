package com.project.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ShopCartItem — DTO đại diện cho một sản phẩm trong giỏ hàng.
 * Lưu thông tin hiển thị: mã sách, tên sách, tác giả, đơn giá, số lượng mua, tồn kho, ảnh bìa.
 */
public class ShopCartItem implements Serializable {

    /** Mã sách */
    private Integer maSach;
    /** Tên sách */
    private String tenSach;
    /** Tác giả */
    private String tacGia;
    /** Đơn giá (giá bán lẻ từng cuốn) */
    private BigDecimal donGia;
    /** Số lượng khách hàng muốn mua */
    private Integer soLuong;
    /** Số lượng tồn kho hiện tại */
    private Integer tonKho;
    /** Đường dẫn ảnh bìa sách */
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

    /**
     * Tính thành tiền = đơn giá * số lượng.
     * Nếu đơn giá hoặc số lượng null thì coi như 0.
     */
    public BigDecimal getThanhTien() {
        BigDecimal gia = donGia == null ? BigDecimal.ZERO : donGia;
        int qty = soLuong == null ? 0 : soLuong;
        return gia.multiply(BigDecimal.valueOf(qty));
    }
}
