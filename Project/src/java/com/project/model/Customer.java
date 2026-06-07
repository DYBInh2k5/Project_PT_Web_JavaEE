// ===== Model Khách hàng (Customer) — lưu thông tin người dùng mua sách =====
// Dùng để đăng nhập, đặt hàng, và quản lý thông tin cá nhân
package com.project.model;

public class Customer {

    // ===== Các thuộc tính của khách hàng =====
    private Integer maKH;      // Mã khách hàng (PK, do DB tự sinh)
    private String tenKH;      // Tên khách hàng
    private String dienThoai;  // Số điện thoại
    private String email;      // Địa chỉ email
    private String diaChi;     // Địa chỉ giao hàng
    private String taiKhoan;   // Tên đăng nhập (username)
    private String matKhau;    // Mật khẩu (lưu dạng plain text — cần cải thiện bảo mật)

    public Integer getMaKH() {
        return maKH;
    }

    public void setMaKH(Integer maKH) {
        this.maKH = maKH;
    }

    public String getTenKH() {
        return tenKH;
    }

    public void setTenKH(String tenKH) {
        this.tenKH = tenKH;
    }

    public String getDienThoai() {
        return dienThoai;
    }

    public void setDienThoai(String dienThoai) {
        this.dienThoai = dienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(String taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }
}
