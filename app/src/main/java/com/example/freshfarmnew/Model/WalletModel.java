package com.example.freshfarmnew.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WalletModel {
    @SerializedName("wallet_id")
    @Expose
    private String walletId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("wallet_balance")
    @Expose
    private String walletBalance;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("UpdatedDate")
    @Expose
    private String updatedDate;
    @SerializedName("CreatedDate")
    @Expose
    private String createdDate;

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(String walletBalance) {
        this.walletBalance = walletBalance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }


}
