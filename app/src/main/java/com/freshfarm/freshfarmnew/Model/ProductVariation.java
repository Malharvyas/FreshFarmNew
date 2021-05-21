package com.freshfarm.freshfarmnew.Model;

public class ProductVariation {
    public String v_id;
    public String product_id;
    public String unit;
    public String price;

    public String getMarket_price() {
        return market_price;
    }

    public void setMarket_price(String market_price) {
        this.market_price = market_price;
    }

    public String getProduct_discount() {
        return product_discount;
    }

    public void setProduct_discount(String product_discount) {
        this.product_discount = product_discount;
    }

    public String unit_val;
    public String market_price;
    public String product_discount;


    public ProductVariation() {
    }

    public ProductVariation(String v_id, String product_id, String unit, String price, String unit_val, String market_price, String product_discount) {
        this.v_id = v_id;
        this.product_id = product_id;
        this.unit = unit;
        this.price = price;
        this.unit_val = unit_val;
        this.market_price = market_price;
        this.product_discount = product_discount;
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
