package com.freshfarm.freshfarmnew.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.freshfarm.freshfarmnew.Adapters.CheckOutAdapter;
import com.freshfarm.freshfarmnew.Class.BaseUrl;
import com.freshfarm.freshfarmnew.Interfaces.AddressCallBack;
import com.freshfarm.freshfarmnew.Model.AddressDataModel;
import com.freshfarm.freshfarmnew.R;
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

import pl.droidsonroids.gif.GifImageView;

public class CheckoutFragment extends Fragment {

    List<AddressDataModel> addressDataModels = new ArrayList<>();
    String url = "", cus_id = "";
    private RecyclerView recyclerView;
    private ProgressBar progressbar;
    CheckOutAdapter checkOutAdapter;
    private TextView tvPrice, tvTotalAmount, item_details;
    private Button btnAddAddress;
    private Button btnContinue, btnPayment;
    private String selectedAddressId = "";
    GifImageView nodata;

    public CheckoutFragment() {
        // Required empty public constructor
    }

    public static CheckoutFragment newInstance(String param1, String param2) {
        CheckoutFragment fragment = new CheckoutFragment();
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
        View v = inflater.inflate(R.layout.fragment_checkout, container, false);

        btnAddAddress = v.findViewById(R.id.btnAddAddress);
        recyclerView = v.findViewById(R.id.recyclerView);
        tvPrice = v.findViewById(R.id.tvPrice);
        item_details = v.findViewById(R.id.item_details);
        tvTotalAmount = v.findViewById(R.id.tvTotalAmount);
        btnContinue = v.findViewById(R.id.btnContinue);
        btnPayment = v.findViewById(R.id.btnPayment);
        progressbar = v.findViewById(R.id.progressbar);
        nodata = v.findViewById(R.id.nodata);

        if (getArguments() != null) {
            if (getArguments().containsKey("amount")) {
                tvTotalAmount.setText(getArguments().getString("amount"));
                tvPrice.setText(getArguments().getString("amount"));
            }
            if (getArguments().containsKey("total_items")) {
                item_details.setText("Price " + " ( " + getArguments().getString("total_items") + " items )");
            }
        }

        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (checkPermission()) {
                AddAddressFragment addressFragment = new AddAddressFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("add_address");
                ft.replace(R.id.fragment_container, addressFragment);
                ft.commit();
                /*} else {
                    Toast.makeText(getContext(), "Please provide location permission to continue", Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("cartdetails", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav);
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                HomeFragment h = new HomeFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("home");
                ft.replace(R.id.fragment_container, h);
                ft.commit();
            }
        });

        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectedAddressId.isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("amount", tvTotalAmount.getText().toString());
                    bundle.putString("addressId", selectedAddressId);

                    Log.e("PrintLog", "===addressId===" + selectedAddressId);

                    PlaceOrderFragment pof = new PlaceOrderFragment();
                    pof.setArguments(bundle);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("PlaceOrder");
                    ft.replace(R.id.fragment_container, pof);
                    ft.commit();
                } else {
                    Toast.makeText(getContext(), "Please Select Address", Toast.LENGTH_LONG).show();
                }
            }
        });

        checkOutAdapter = new CheckOutAdapter(getContext(), addressDataModels, new AddressCallBack() {
            @Override
            public void removeAddress(int position, String addressId) {
                remvoeAddress(position, addressId);
            }

            @Override
            public void updateAddress(AddressDataModel addressDataModel) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", addressDataModel);
                AddAddressFragment addressFragment = new AddAddressFragment();
                addressFragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("Cart");
                ft.replace(R.id.fragment_container, addressFragment);
                ft.commit();
            }

            @Override
            public void onAddressSelect(String id) {

                selectedAddressId = id;
            }
        });
        recyclerView.setAdapter(checkOutAdapter);

        getAddressData();

        return v;
    }

   /* private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }*/

    private void remvoeAddress(int position, String addressId) {
        progressbar.setVisibility(View.VISIBLE);
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/removeAddress");
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressbar.setVisibility(View.GONE);
                        BaseUrl b = new BaseUrl();
                        url = b.url;
                        if (response != null) {
                            JSONObject json = null;
                            try {
                                json = new JSONObject(String.valueOf(response));
                                JSONObject json2 = json.getJSONObject("removeAddress");
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                if (stat.equals("true")) {
                                    String msg = json2.getString("Message");
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                    addressDataModels.remove(position);
                                    if (addressDataModels.isEmpty()) {
//                                        recyclerView.setVisibility(View.GONE);
                                        nodata.setVisibility(View.VISIBLE);
                                        checkOutAdapter.notifyDataSetChanged();
                                    } else {
                                        checkOutAdapter.notifyDataSetChanged();
                                    }
                                } else if (stat.equals("false")) {
                                    String msg = json2.getString("Message");
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                }
//                                Toast.makeText(getApplicationContext(),""+response,Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressbar.setVisibility(View.GONE);
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("address_id", addressId);

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

    private void getAddressData() {
        progressbar.setVisibility(View.VISIBLE);
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/getAddress");
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressbar.setVisibility(View.GONE);
                        Log.e("PrintLog", "----" + response);
                        BaseUrl b = new BaseUrl();
                        url = b.url;
                        if (response != null) {
                            JSONObject json = null;

                            try {
                                json = new JSONObject(String.valueOf(response));
                                JSONObject json2 = json.getJSONObject("getAddress");
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                if (stat.equals("true")) {
                                    addressDataModels.clear();

                                    JSONArray data = json2.getJSONArray("data");
                                    String dataStr = data.toString();
                                    Log.e("PrintLog", "----" + dataStr);
                                    Gson gson = new Gson();

                                    Type cartListType = new TypeToken<List<AddressDataModel>>() {
                                    }.getType();

                                    addressDataModels.addAll(gson.fromJson(dataStr, cartListType));
                                    if (addressDataModels.isEmpty()) {
//                                        recyclerView.setVisibility(View.GONE);
                                        nodata.setVisibility(View.VISIBLE);
                                    } else {
                                        checkOutAdapter.notifyDataSetChanged();
                                    }


                                    //calculateTotalAmount(addressDataModels);

                                    //Toast.makeText(getContext(), "" + userArray.size(), Toast.LENGTH_LONG).show();

                                } else if (stat.equals("false")) {
                                    String msg = json2.getString("Message");
//                                    recyclerView.setVisibility(View.GONE);
                                    nodata.setVisibility(View.VISIBLE);
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
                Log.e("PrintLog", "----" + error);
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
                params.put("customer_id", cus_id);
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