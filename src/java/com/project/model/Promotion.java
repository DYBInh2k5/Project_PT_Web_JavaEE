package com.project.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

public class Promotion implements Serializable {

    private Integer maKM;
    private String tenKM;
    private String hinhThuc;
    private BigDecimal giaTri;
    private String maCoupon;
    private Date ngayBD;
    private Date ngayKT;

    public Integer getMaKM() {
        return maKM;
    }

    public void setMaKM(Integer maKM) {
        this.maKM = maKM;
    }

    public String getTenKM() {
        return tenKM;
    }

    public void setTenKM(String tenKM) {
        this.tenKM = tenKM;
    }

    public String getHinhThuc() {
        return hinhThuc;
    }

    public void setHinhThuc(String hinhThuc) {
        this.hinhThuc = hinhThuc;
    }

    public BigDecimal getGiaTri() {
        return giaTri;
    }

    public void setGiaTri(BigDecimal giaTri) {
        this.giaTri = giaTri;
    }

    public String getMaCoupon() {
        return maCoupon;
    }

    public void setMaCoupon(String maCoupon) {
        this.maCoupon = maCoupon;
    }

    public Date getNgayBD() {
        return ngayBD;
    }

    public void setNgayBD(Date ngayBD) {
        this.ngayBD = ngayBD;
    }

    public Date getNgayKT() {
        return ngayKT;
    }

    public void setNgayKT(Date ngayKT) {
        this.ngayKT = ngayKT;
    }
}