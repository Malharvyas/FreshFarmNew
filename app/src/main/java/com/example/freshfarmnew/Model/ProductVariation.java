package com.example.freshfarmnew.Model;

public class ProductVariation {
    public String v_id;
    public String product_id;
    public String unit;
    public String price;
    public String unit_val;

    public ProductVariation() {
    }

    public ProductVariation(String v_id, String product_id, String unit, String price, String unit_val) {
        this.v_id = v_id;
        this.product_id = product_id;
        this.unit = unit;
        this.price = price;
        this.unit_val = unit_val;
    }

    public String getV_id() {
        return v_id;
    }

    public void setV_id(String v_id) {
        this.v_id = v_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
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

    public String getUnit_val() {
        return unit_val;
    }

    public void setUnit_val(String unit_val) {
        this.unit_val = unit_val;
    }
}
