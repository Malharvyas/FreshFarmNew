package com.freshfarm.freshfarmnew.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.freshfarm.freshfarmnew.Adapters.AreaAdapter;
import com.freshfarm.freshfarmnew.Class.BaseUrl;
import com.freshfarm.freshfarmnew.MapsActivity;
import com.freshfarm.freshfarmnew.Model.AddressDataModel;
import com.freshfarm.freshfarmnew.Model.AddressModel;
import com.freshfarm.freshfarmnew.Model.AreaModel;
import com.freshfarm.freshfarmnew.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddAddressFragment extends Fragment {

    ArrayList<AreaModel> areaModelList = new ArrayList<>();
    AutoCompleteTextView autoCompleteText;
    AddressModel addressModel;
    String url = "", cus_id = "", latitude = "0.0", longitude = "0.0", name = "", phoneNumber = "", address = "", type = "1";
    private Button btnAddAddress;
    private EditText edName, edAddress, edPhone;
    private RadioGroup radioGroup;
    private AddressDataModel addressDataModel;
    private RadioButton radioBtnHome;
    private RadioButton radioBtnOffice;
    SupportMapFragment mapFragment;
    private static final int REQUEST_CODE = 101;
    SharedPreferences sharedPreferences, sharedPreferences2, getpreferances;
    private AreaModel areaModel;

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

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        edName = v.findViewById(R.id.edName);
        edAddress = v.findViewById(R.id.edAddress);
        edPhone = v.findViewById(R.id.edPhone);
        btnAddAddress = v.findViewById(R.id.btnAddAddress);
        radioGroup = v.findViewById(R.id.radioGroup);
        radioBtnHome = v.findViewById(R.id.radioBtnHome);
        radioBtnOffice = v.findViewById(R.id.radioBtnOffice);
        autoCompleteText = v.findViewById(R.id.autoCompleteText);

        autoCompleteText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                if (item instanceof AreaModel) {
                    areaModel = (AreaModel) item;
                    edAddress.setText(areaModel.getAreaName());
                }
            }
        });

        if (getArguments() != null) {
            if (getArguments().containsKey("data")) {
                addressDataModel = (AddressDataModel) getArguments().getSerializable("data");
                if (addressDataModel != null) {
                    edName.setText(addressDataModel.getContactName());
                    edPhone.setText(addressDataModel.getPhoneNumber());
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

        getArea();

        return v;
    }

    private void getAddressDetails() {
        address = edAddress.getText().toString();
        name = edName.getText().toString();
        phoneNumber = edPhone.getText().toString();
        if (name.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(getContext(), "Please fill up all the details", Toast.LENGTH_SHORT).show();
        } else {
            //SharedPreferences ss = getActivity().getSharedPreferences("location", Context.MODE_PRIVATE);
            //latitude = ss.getString("lat", "0.0");
            //longitude = ss.getString("long", "0.0");
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
                            Log.e("printLog", "----updateAddress_response----" + response);
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
                    /*params.put("latitude", latitude);
                    params.put("longitude", longitude);*/
                    params.put("address", address);
                    params.put("type", type);
                    params.put("phone_number", phoneNumber);
                    params.put("contact_name", name);
                    if (areaModel != null)
                        params.put("area_id", areaModel.getAreaId());
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

    private void getArea() {
        //progressBar.setVisibility(View.VISIBLE);
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/getArea");
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("printLog", "---getArea_response---" + response);
                        //progressBar.setVisibility(View.GONE);
                        BaseUrl b = new BaseUrl();
                        url = b.url;
                        if (response != null) {
                            JSONObject json = null;

                            try {
                                Gson gson = new Gson();

                                Type areaListType = new TypeToken<List<AreaModel>>() {
                                }.getType();

                                areaModelList.addAll(gson.fromJson(response, areaListType));

                                AreaAdapter areaAdapter = new AreaAdapter(getContext(), R.layout.item_area, areaModelList);
                                autoCompleteText.setThreshold(1);
                                autoCompleteText.setAdapter(areaAdapter);
                                autoCompleteText.setTextColor(Color.RED);
                                Log.e("printLog", "---areaModelList---" + areaModelList.size());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressBar.setVisibility(View.GONE);
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
                //params.put("customer_id", cus_id);
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

    @Override
    public void onPause() {
        super.onPause();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }
}