package com.example.freshfarmnew.Model;

import java.util.List;

public class Product {
    public String product_id;
    public String SKU;
    public String product_discription;
    public String category_id;
    public String subcat_id;
    public String product_name;
    public String product_image;
    public String product_price;
    public Boolean liked;
    public List<ProductVariation> variations;

    public Product() {
    }

    public Product(String product_id, String SKU, String product_discription, String category_id, String subcat_id, String product_name, String product_image, String product_price, Boolean liked, List<ProductVariation> variations) {
        this.product_id = product_id;
        this.SKU = SKU;
        this.product_discription = product_discription;
        this.category_id = category_id;
        this.subcat_id = subcat_id;
        this.product_name = product_name;
        this.product_image = product_image;
        this.product_price = product_price;
        this.liked = liked;
        this.variations = variations;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public List<ProductVariation> getVariations() {
        return variations;
    }

    public void setVariations(List<ProductVariation> variations) {
        this.variations = variations;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public String getProduct_discription() {
        return product_discription;
    }

    public void setProduct_discription(String product_discription) {
        this.product_discription = product_discription;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getSubcat_id() {
        return subcat_id;
    }

    public void setSubcat_id(String subcat_id) {
        this.subcat_id = subcat_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }
}
