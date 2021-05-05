package com.example.freshfarmnew.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderDetailsModel {

    @SerializedName("customer_id")
    @Expose
    private String customerId;
    @SerializedName("address_id")
    @Expose
    private String addressId;
    @SerializedName("total_amount")
    @Expose
    private String totalAmount;

    public String getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    @SerializedName("delivery_charge")
    @Expose
    private String deliveryCharge;
    @SerializedName("tax")
    @Expose
    private Object tax;
    @SerializedName("discount")
    @Expose
    private String discount;

    public AddressDetails getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(AddressDetails addressDetail) {
        this.addressDetail = addressDetail;
    }

    @SerializedName("customer_detail")
    @Expose
    private CustomerDetail customerDetail;
    @SerializedName("address_detail")
    @Expose
    private AddressDetails addressDetail;
    @SerializedName("product_detail")
    @Expose
    private List<ProductDetail> productDetail = null;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Object getTax() {
        return tax;
    }

    public void setTax(Object tax) {
        this.tax = tax;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public CustomerDetail getCustomerDetail() {
        return customerDetail;
    }

    public void setCustomerDetail(CustomerDetail customerDetail) {
        this.customerDetail = customerDetail;
    }

    public List<ProductDetail> getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(List<ProductDetail> productDetail) {
        this.productDetail = productDetail;
    }
}
