package com.project.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class ReturnTransaction implements Serializable {

    private int maDT;
    private int maHD;
    private Timestamp ngayDoi;
    private String lyDo;
    private String ghiChu;
    private int kieuXuLy;
    private String tenKH;
    private BigDecimal tongTienHoaDon;

    public int getMaDT() {
        return maDT;
    }

    public void setMaDT(int maDT) {
        this.maDT = maDT;
    }

    public int getMaHD() {
        return maHD;
    }

    public void setMaHD(int maHD) {
        this.maHD = maHD;
    }

    public Timestamp getNgayDoi() {
        return ngayDoi;
    }

    public void setNgayDoi(Timestamp ngayDoi) {
        this.ngayDoi = ngayDoi;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public int getKieuXuLy() {
        return kieuXuLy;
    }

    public void setKieuXuLy(int kieuXuLy) {
        this.kieuXuLy = kieuXuLy;
    }

    public String getTenKH() {
        return tenKH;
    }

    public void setTenKH(String tenKH) {
        this.tenKH = tenKH;
    }

    public BigDecimal getTongTienHoaDon() {
        return tongTienHoaDon;
    }

    public void setTongTienHoaDon(BigDecimal tongTienHoaDon) {
        this.tongTienHoaDon = tongTienHoaDon;
    }
}