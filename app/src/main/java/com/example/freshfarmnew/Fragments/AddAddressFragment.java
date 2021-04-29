package com.example.freshfarmnew.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.freshfarmnew.Adapters.CartAdapter;
import com.example.freshfarmnew.Class.BaseUrl;
import com.example.freshfarmnew.Interfaces.CartCallBack;
import com.example.freshfarmnew.Model.AddressDataModel;
import com.example.freshfarmnew.Model.AddressModel;
import com.example.freshfarmnew.Model.CartModel;
import com.example.freshfarmnew.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAddressFragment extends Fragment {

    AddressModel addressModel;
    String url = "", cus_id = "", latitude = "0.0", longitude = "0.0", address = "", type = "1";
    private Button btnAddAddress;
    private EditText edName, edAddress, edPhone;
    private RadioGroup radioGroup;
    private AddressDataModel addressDataModel;
    private RadioButton radioBtnHome;
    private RadioButton radioBtnOffice;

    public AddAddressFragment() {
        // Required empty public constructor
    }

    public static AddAddressFragment newInstance(String param1, String param2) {
        AddAddressFragment fragment = new AddAddressFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userpref", Context.MODE_PRIVATE);
        cus_id = sharedPreferences.getString("customer_id", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_address, container, false);

        edName = v.findViewById(R.id.edName);
        edAddress = v.findViewById(R.id.edAddress);
        edPhone = v.findViewById(R.id.edPhone);
        btnAddAddress = v.findViewById(R.id.btnAddAddress);
        radioGroup = v.findViewById(R.id.radioGroup);
        radioBtnHome = v.findViewById(R.id.radioBtnHome);
        radioBtnOffice = v.findViewById(R.id.radioBtnOffice);
        if (getArguments() != null) {
            if (getArguments().containsKey("data")) {
                addressDataModel = (AddressDataModel) getArguments().getSerializable("data");
                if (addressDataModel != null) {
                    edAddress.setText(addressDataModel.getAddress());
                    if (addressDataModel.getType().equals("2"))
                        radioBtnOffice.setChecked(true);
                    else
                        radioBtnHome.setChecked(true);
                }
            }
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioBtnHome:
                        type = "1";
                        break;
                    case R.id.radioBtnOffice:
                        type = "2";
                        break;
                }
            }
        });

        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getAddressDetails();
            }
        });

        return v;
    }

    private void getAddressDetails() {
        address = edAddress.getText().toString();
        // progressbar.setVisibility(View.VISIBLE);
        BaseUrl b = new BaseUrl();
        url = b.url;
        if (addressDataModel != null)
            url = url.concat("freshfarm/api/ApiController/updateAddress");
        else
            url = url.concat("freshfarm/api/ApiController/addAddress");
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressbar.setVisibility(View.GONE);
                        Log.e("PrintLog", "----" + response);
                        BaseUrl b = new BaseUrl();
                        url = b.url;
                        if (response != null) {
                            JSONObject json = null;

                            try {
                                json = new JSONObject(String.valueOf(response));
                                JSONObject json2;
                                if (addressDataModel != null) {
                                    json2 = json.getJSONObject("updateAddress");
                                } else {
                                    json2 = json.getJSONObject("AddAddress");
                                }
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                if (stat.equals("true")) {
                                    String msg = json2.getString("Message");
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                    getActivity().getSupportFragmentManager().popBackStack();
                                } else if (stat.equals("false")) {
                                    String msg = json2.getString("Message");
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // progressbar.setVisibility(View.GONE);
                BaseUrl b = new BaseUrl();
                url = b.url;
                if (error instanceof ClientError) {
                    try {
                        String responsebody = new String(error.networkResponse.data, "utf-8");
                        JSONObject data = new JSONObject(responsebody);
                        Boolean status = data.getBoolean("status");
                        String stat = status.toString();
                        if (stat.equals("false")) {
                            String msg = data.getString("Message");
                            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "Error : " + error, Toast.LENGTH_SHORT).show();
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (addressDataModel != null)
                    params.put("address_id", addressDataModel.getAddressId());
                params.put("customer_id", cus_id);
                params.put("latitude", latitude);
                params.put("longitude", longitude);
                params.put("address", address);
                params.put("type", type);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "u222436058_fresh_farm:tG9r6C5Q$";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", auth);
//                headers.put("x-api-key","HRCETCRACKER@123");
//                headers.put("Content-Type", "application/form-data");
                return headers;
            }
        };
        volleyRequestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
    }
}