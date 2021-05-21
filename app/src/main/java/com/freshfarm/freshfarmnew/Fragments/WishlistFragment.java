package com.freshfarm.freshfarmnew.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.freshfarm.freshfarmnew.Adapters.WishListAdapter;
import com.freshfarm.freshfarmnew.Class.BaseUrl;
import com.freshfarm.freshfarmnew.Interfaces.WishListCallBack;
import com.freshfarm.freshfarmnew.Model.WishListModel;
import com.freshfarm.freshfarmnew.Model.WishListVariation;
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

public class WishlistFragment extends Fragment {

    List<WishListModel> wishListModelList = new ArrayList<>();
    List<WishListVariation> variationList;
    String url = "", cus_id = "";
    private ConstraintLayout emptyWhishList;
    private LinearLayout nonemptyWhishList;
    private RecyclerView recyclerView;
    private ProgressBar progressbar;
    private Button btnExplore;
    WishListAdapter wishListAdapter;

    public WishlistFragment() {
        // Required empty public constructor
    }

    public static WishlistFragment newInstance(String param1, String param2) {
        WishlistFragment fragment = new WishlistFragment();
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
        View v = inflater.inflate(R.layout.fragment_wishlist, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        progressbar = v.findViewById(R.id.progressbar);
        emptyWhishList = v.findViewById(R.id.emptyWhishList);
        nonemptyWhishList = v.findViewById(R.id.nonemptyWhishList);
        btnExplore = v.findViewById(R.id.btnExplore);

        wishListAdapter = new WishListAdapter(getContext(), wishListModelList, new WishListCallBack() {
            @Override
            public void updateWishList(int position, String productId, String cust_id) {
                getRemoveWishList(position, productId, cust_id);
            }
        });

        btnExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav);
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                HomeFragment h = new HomeFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("home");
                ft.replace(R.id.fragment_container, h);
                ft.commit();
            }
        });
        recyclerView.setAdapter(wishListAdapter);

        getWishListData();

        return v;
    }

    private void getRemoveWishList(int position, String product_id, String cust_id) {
        progressbar.setVisibility(View.VISIBLE);
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/addtowishlist");
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

                                    String msg = json2.getString("Message");
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

                                    wishListModelList.remove(position);

                                    if (wishListModelList.size() > 0) {
                                        nonemptyWhishList.setVisibility(View.VISIBLE);
                                        emptyWhishList.setVisibility(View.GONE);

                                        wishListAdapter.notifyDataSetChanged();
                                    } else {
                                        emptyWhishList.setVisibility(View.VISIBLE);
                                        nonemptyWhishList.setVisibility(View.GONE);
                                    }

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
                BaseUrl b = new BaseUrl();
                url = b.url;
                progressbar.setVisibility(View.GONE);
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
                params.put("customer_id", cust_id);
                params.put("product_id", product_id);
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

    private void getWishListData() {
        progressbar.setVisibility(View.VISIBLE);
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/WishList");
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
                                JSONObject json2 = json.getJSONObject("WishList");
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                if (stat.equals("true")) {

                                    JSONArray data = json2.getJSONArray("data");
                                    String dataStr = data.toString();
                                    Log.e("PrintLog", "----" + dataStr);
                                    Gson gson = new Gson();

                                    Type cartListType = new TypeToken<List<WishListModel>>() {
                                    }.getType();

                                    wishListModelList.addAll(gson.fromJson(dataStr, cartListType));

                                    if (wishListModelList.size() > 0) {
                                        nonemptyWhishList.setVisibility(View.VISIBLE);
                                        emptyWhishList.setVisibility(View.GONE);

                                        wishListAdapter.notifyDataSetChanged();

                                    } else {
                                        emptyWhishList.setVisibility(View.VISIBLE);
                                        nonemptyWhishList.setVisibility(View.GONE);
                                    }

                                    //Toast.makeText(getContext(), "" + userArray.size(), Toast.LENGTH_LONG).show();

                                } else if (stat.equals("false")) {
                                    emptyWhishList.setVisibility(View.VISIBLE);
                                    nonemptyWhishList.setVisibility(View.GONE);

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
}