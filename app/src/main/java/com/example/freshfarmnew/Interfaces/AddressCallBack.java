package com.example.freshfarmnew.Interfaces;

import com.example.freshfarmnew.Model.AddressDataModel;

public interface AddressCallBack {
    void removeAddress(int position, String addressId);

    void updateAddress(AddressDataModel addressDataModel);

    void onAddressSelect(String id);
}
