package com.freshfarm.freshfarmnew.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderListModel {

    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("total_amount")
    @Expose
    private String totalAmount;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("order_time")
    @Expose
    private String orderTime;
    @SerializedName("products")
    @Expose
    private List<Product> products = null;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

}
