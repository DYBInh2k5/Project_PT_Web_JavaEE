// ===== Model Người dùng (AuthUser) — dùng cho xác thực admin/nhân viên =====
// Serializable để lưu vào session (đăng nhập)
package com.project.model;

import java.io.Serializable;

public class AuthUser implements Serializable {

    // ===== Các thuộc tính của người dùng hệ thống =====
    private String maNV;      // Mã nhân viên (PK)
    private String hoTen;     // Họ và tên nhân viên
    private String taiKhoan;  // Tên đăng nhập
    private String vaiTro;    // Vai trò: "admin" hoặc "nhanvien"

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(String taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }
}
