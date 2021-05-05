package com.example.freshfarmnew.Fragments;

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
import com.example.freshfarmnew.Adapters.CheckOutAdapter;
import com.example.freshfarmnew.Adapters.OrderDetailsAdapter;
import com.example.freshfarmnew.Class.BaseUrl;
import com.example.freshfarmnew.Interfaces.AddressCallBack;
import com.example.freshfarmnew.Interfaces.CartCallBack;
import com.example.freshfarmnew.Model.AddressDataModel;
import com.example.freshfarmnew.Model.CartModel;
import com.example.freshfarmnew.Model.OrderDetailsModel;
import com.example.freshfarmnew.Model.ProductDetail;
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

public class OrderDetailsFragment extends Fragment {

    List<OrderDetailsModel> odList = new ArrayList<>();
    List<ProductDetail> productDetailList = new ArrayList<>();
    String url = "", cus_id = "", orderId = "";

    OrderDetailsAdapter orderDetailsAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressbar;
    private TextView tvOrderID, tvUserName, tvEmail, tvContactNumber, tvUserName1, tvAddres, tvTotalAmount, tvDeliveryCharge, tvFinalPayAmount;

    public OrderDetailsFragment() {
        // Required empty public constructor
    }

    public static OrderDetailsFragment newInstance(String param1, String param2) {
        OrderDetailsFragment fragment = new OrderDetailsFragment();
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
        View v = inflater.inflate(R.layout.fragment_order_details, container, false);

        progressbar = v.findViewById(R.id.progressbar);
        recyclerView = v.findViewById(R.id.recyclerView);
        tvOrderID = v.findViewById(R.id.tvOrderID);
        tvUserName = v.findViewById(R.id.tvUserName);
        tvEmail = v.findViewById(R.id.tvEmail);
        tvContactNumber = v.findViewById(R.id.tvContactNumber);
        tvUserName1 = v.findViewById(R.id.tvUserName1);
        tvAddres = v.findViewById(R.id.tvAddres);
        tvTotalAmount = v.findViewById(R.id.tvTotalAmount);
        tvDeliveryCharge = v.findViewById(R.id.tvDeliveryCharge);
        tvFinalPayAmount = v.findViewById(R.id.tvFinalPayAmount);

        if (getArguments() != null) {
            if (getArguments().containsKey("OrderId")) {
                orderId = getArguments().getString("OrderId");
                tvOrderID.setText(getArguments().getString("OrderId"));
            }
        }

        orderDetailsAdapter = new OrderDetailsAdapter(getContext(), productDetailList, new CartCallBack() {
            @Override
            public void updateCart(String productId, String vId, String quantity) {

            }
        });
        recyclerView.setAdapter(orderDetailsAdapter);

        getOrderDetails();

        return v;
    }

    private void getOrderDetails() {
        progressbar.setVisibility(View.VISIBLE);
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/getOrderDetail");
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressbar.setVisibility(View.GONE);
                        Log.e("PrintLog", "----response----" + response);
                        BaseUrl b = new BaseUrl();
                        url = b.url;
                        if (response != null) {
                            JSONObject json = null;

                            try {
                                json = new JSONObject(String.valueOf(response));
                                JSONObject json2 = json.getJSONObject("getOrderDetail");
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                Log.e("PrintLog", "----stat----" + stat);
                                if (stat.equals("true")) {

                                    odList.clear();

                                    JSONArray data = json2.getJSONArray("data");
                                    String dataStr = data.toString();
                                    Log.e("PrintLog", "----dataStr----" + dataStr);
                                    Gson gson = new Gson();

                                    Type cartListType = new TypeToken<List<OrderDetailsModel>>() {
                                    }.getType();

                                    odList.addAll(gson.fromJson(dataStr, cartListType));

                                    OrderDetailsModel orderDetailsModel = odList.get(0);

                                    tvUserName.setText(orderDetailsModel.getCustomerDetail().getCustomerName());
                                    tvEmail.setText(orderDetailsModel.getCustomerDetail().getCustomerEmail());
                                    tvContactNumber.setText(orderDetailsModel.getCustomerDetail().getCustomerPhone());
                                    tvUserName1.setText(orderDetailsModel.getCustomerDetail().getCustomerName());
                                    tvTotalAmount.setText(getContext().getResources().getString(R.string.rs) + " " + orderDetailsModel.getTotalAmount());
                                    tvDeliveryCharge.setText(getContext().getResources().getString(R.string.rs) + " " + orderDetailsModel.getDeliveryCharge());
                                    if (orderDetailsModel.getTotalAmount() != null && orderDetailsModel.getDeliveryCharge() != null) {
                                        double total = Double.parseDouble(orderDetailsModel.getTotalAmount()) + Double.parseDouble(orderDetailsModel.getDeliveryCharge());
                                        tvFinalPayAmount.setText(getContext().getResources().getString(R.string.rs) + " " + String.valueOf(total));
                                    }

                                    if (orderDetailsModel.getAddressDetail() != null) {
                                        tvAddres.setText(orderDetailsModel.getAddressDetail().getAddress());
                                    }

                                    productDetailList.clear();
                                    productDetailList.addAll(orderDetailsModel.getProductDetail());

                                    orderDetailsAdapter.notifyDataSetChanged();

                                } else if (stat.equals("false")) {
                                    String msg = json2.getString("Message");
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
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
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("order_id", orderId);
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