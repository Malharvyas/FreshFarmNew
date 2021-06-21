package com.freshfarm.freshfarmnew.Interfaces;

import com.freshfarm.freshfarmnew.Model.AddressDataModel;

public interface AddressCallBack {
    void removeAddress(int position, String addressId);

    void updateAddress(AddressDataModel addressDataModel);

    void onAddressSelect(String id);
    void onAddressSelect1(String address);
}
