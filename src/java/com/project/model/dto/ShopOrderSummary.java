package com.project.model.dto;

import com.project.model.Invoice;
import java.io.Serializable;

public class ShopOrderSummary implements Serializable {

    private Invoice invoice;
    private int itemCount;
    private String orderCode;

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }
}