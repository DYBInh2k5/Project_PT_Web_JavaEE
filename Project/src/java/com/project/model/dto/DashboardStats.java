package com.project.model.dto;

import java.math.BigDecimal;

/**
 * DashboardStats — DTO chứa các chỉ số thống kê tổng quan cho trang dashboard admin.
 * Bao gồm: tổng số sách, tổng số khách hàng, tổng số hóa đơn và tổng doanh thu.
 */
public class DashboardStats {

    /** Tổng số sách trong cửa hàng */
    private int totalBooks;
    /** Tổng số khách hàng đã đăng ký */
    private int totalCustomers;
    /** Tổng số hóa đơn (đơn hàng) */
    private int totalInvoices;
    /** Tổng doanh thu */
    private BigDecimal revenue;

    public int getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(int totalBooks) {
        this.totalBooks = totalBooks;
    }

    public int getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(int totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public int getTotalInvoices() {
        return totalInvoices;
    }

    public void setTotalInvoices(int totalInvoices) {
        this.totalInvoices = totalInvoices;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }
}
