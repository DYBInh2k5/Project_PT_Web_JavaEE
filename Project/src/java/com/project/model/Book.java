// ===== Entity Sách (Book) — ánh xạ với bảng Sach trong CSDL =====
// Mỗi instance của Book tương ứng với một dòng trong bảng Sach
package com.project.model;

import java.math.BigDecimal;
import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// @Entity: class này là một thực thể JPA, được quản lý bởi Hibernate
// @Table(name = "Sach"): ánh xạ tới bảng "Sach" trong SQL Server
@Entity
@Table(name = "Sach")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    // ===== Các trường (cột) trong bảng Sach =====

    // @Id: khóa chính của bảng
    // @GeneratedValue: tự động tăng (IDENTITY trong SQL Server)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaSach")     // Mã sách (int, PK)
    private Integer maSach;

    @Column(name = "TenSach")    // Tên sách (nvarchar)
    private String tenSach;

    @Column(name = "TacGia")     // Tác giả (nvarchar)
    private String tacGia;

    @Column(name = "TheLoai")    // Thể loại (nvarchar)
    private String theLoai;

    @Column(name = "DonGia")     // Đơn giá (decimal/money)
    private BigDecimal donGia;

    @Column(name = "SoLuong")    // Số lượng tồn kho (int)
    private Integer soLuong;

    @Column(name = "AnhBia")     // Đường dẫn ảnh bìa (nvarchar)
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

    public String getTheLoai() {
        return theLoai;
    }

    public void setTheLoai(String theLoai) {
        this.theLoai = theLoai;
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

    public String getAnhBia() {
        return anhBia;
    }

    public void setAnhBia(String anhBia) {
        this.anhBia = anhBia;
    }
}
