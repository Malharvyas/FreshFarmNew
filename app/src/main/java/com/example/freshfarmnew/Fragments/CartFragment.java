package com.example.freshfarmnew.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.freshfarmnew.Model.CartModel;
import com.example.freshfarmnew.R;
import com.google.android.material.badge.BadgeDrawable;
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

public class CartFragment extends Fragment {

    List<CartModel> cartModelList = new ArrayList<>();
    String url = "", cus_id = "";
    private RecyclerView recyclerView;
    private ProgressBar progressbar;
    private ConstraintLayout emptyCart;
    private LinearLayout nonemptyCart;
    CartAdapter cartAdapter;
    private TextView tvTotalAmount;
    private Button btnAddItem;
    private Button btnContinue, btnCheckout;

    public CartFragment() {
        // Required empty public constructor
    }

    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
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
        View v = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);

        progressbar = v.findViewById(R.id.progressbar);
        emptyCart = v.findViewById(R.id.empty_cart);
        nonemptyCart = v.findViewById(R.id.nonempty_cart);
        tvTotalAmount = v.findViewById(R.id.tvTotalAmount);
        btnAddItem = v.findViewById(R.id.add_item);
        btnContinue = v.findViewById(R.id.btnContinue);
        btnCheckout = v.findViewById(R.id.btnCheckout);

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("amount", tvTotalAmount.getText().toString());
                CheckoutFragment checkoutFragment = new CheckoutFragment();
                checkoutFragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("Cart");
                ft.replace(R.id.fragment_container, checkoutFragment);
                ft.commit();
            }
        });

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav);
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                HomeFragment h = new HomeFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("home");
                ft.replace(R.id.fragment_container, h);
                ft.commit();
            }
        });

        cartAdapter = new CartAdapter(getContext(), cartModelList, new CartCallBack() {
            @Override
            public void updateCart(String productId, String vId, String quantity) {
                addToCart(productId, vId, quantity);
            }
        });
        recyclerView.setAdapter(cartAdapter);

        getCartDetails();

        return v;
    }

    private void getCartDetails() {
        progressbar.setVisibility(View.VISIBLE);
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/cartdata");
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
                                JSONObject json2 = json.getJSONObject("cartdata");
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                if (stat.equals("true")) {
                                    cartModelList.clear();

                                    JSONArray data = json2.getJSONArray("data");
                                    String dataStr = data.toString();
                                    Log.e("PrintLog", "----" + dataStr);
                                    Gson gson = new Gson();

                                    Type cartListType = new TypeToken<List<CartModel>>() {
                                    }.getType();

                                    cartModelList.addAll(gson.fromJson(dataStr, cartListType));

                                    calculateTotalAmount(cartModelList);

                                    if (cartModelList.size() > 0) {
                                        nonemptyCart.setVisibility(View.VISIBLE);
                                        emptyCart.setVisibility(View.GONE);

                                        cartAdapter.notifyDataSetChanged();

                                    } else {
                                        emptyCart.setVisibility(View.VISIBLE);
                                        nonemptyCart.setVisibility(View.GONE);
                                    }

                                    //Toast.makeText(getContext(), "" + userArray.size(), Toast.LENGTH_LONG).show();

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

    private void addToCart(String productId, String vId, String quantity) {
        progressbar.setVisibility(View.VISIBLE);
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/addtocart");
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
                                Log.e("PrintLog", "---------response--------" + response);
                                JSONObject json2 = json.getJSONObject("addtocart");
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                if (stat.equals("true")) {
                                    cartModelList.clear();

                                    JSONArray data = json2.getJSONArray("data");
                                    String dataStr = data.toString();
                                    Log.e("PrintLog", "----" + dataStr);
                                    Gson gson = new Gson();

                                    Type cartListType = new TypeToken<List<CartModel>>() {
                                    }.getType();

                                    cartModelList.addAll(gson.fromJson(dataStr, cartListType));

                                    calculateTotalAmount(cartModelList);

                                    if (cartModelList.size() > 0) {
                                        nonemptyCart.setVisibility(View.VISIBLE);
                                        emptyCart.setVisibility(View.GONE);

                                        cartAdapter.notifyDataSetChanged();
                                    } else {
                                        emptyCart.setVisibility(View.VISIBLE);
                                        nonemptyCart.setVisibility(View.GONE);
                                    }

                                    if (quantity.equals("1")) {
                                        String msg = json2.getString("Message");
                                        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                    }
                                    if (quantity.equals("0")) {
                                        String msg = "Item Removed Successfully";
                                        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
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
                params.put("customer_id", cus_id);
                params.put("product_id", productId);
                params.put("v_id", vId);
                params.put("quantity", quantity);
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

    private void calculateTotalAmount(List<CartModel> cartModelList) {
        double totleAmount = 0.0;
        for (int i = 0; i < cartModelList.size(); i++) {
            if (cartModelList.get(i).getPrice() != null && cartModelList.get(i).getQuantity() != null) {
                double finalPrice = Double.parseDouble(cartModelList.get(i).getPrice()) * Double.parseDouble(cartModelList.get(i).getQuantity());
                totleAmount = totleAmount + finalPrice;
            }
        }
        tvTotalAmount.setText("" + totleAmount);
    }
}