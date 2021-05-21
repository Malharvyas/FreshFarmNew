package com.freshfarm.freshfarmnew.Model;

public class SubCategory {
    public String subcat_id;
    public String subcategory_name;
    public String category_id;
    public String image;

    public SubCategory() {
    }

    public SubCategory(String subcat_id, String subcategory_name, String category_id, String image) {
        this.subcat_id = subcat_id;
        this.subcategory_name = subcategory_name;
        this.category_id = category_id;
        this.image = image;
    }

    public String getSubcat_id() {
        return subcat_id;
    }

    public void setSubcat_id(String subcat_id) {
        this.subcat_id = subcat_id;
    }

    public String getSubcategory_name() {
        return subcategory_name;
    }

    public void setSubcategory_name(String subcategory_name) {
        this.subcategory_name = subcategory_name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
