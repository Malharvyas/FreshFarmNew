package com.freshfarm.freshfarmnew.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WishListVariation {

    @SerializedName("v_id")
    @Expose
    private String vId;
    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("unit_val")
    @Expose
    private String unitVal;
    @SerializedName("CreatedDate")
    @Expose
    private String createdDate;
    @SerializedName("UpdatedDate")
    @Expose
    private String updatedDate;
    @SerializedName("status")
    @Expose
    private String status;

    public String getvId() {
        return vId;
    }

    public void setvId(String vId) {
        this.vId = vId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnitVal() {
        return unitVal;
    }

    public void setUnitVal(String unitVal) {
        this.unitVal = unitVal;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
