package com.freshfarm.freshfarmnew.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddressDetails {
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("landmark")
    @Expose
    private Object landmark;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Object getLandmark() {
        return landmark;
    }

    public void setLandmark(Object landmark) {
        this.landmark = landmark;
    }
}
