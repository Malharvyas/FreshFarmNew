package com.freshfarm.freshfarmnew.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.freshfarm.freshfarmnew.Adapters.MyOrdersAdapter;
import com.freshfarm.freshfarmnew.Class.BaseUrl;
import com.freshfarm.freshfarmnew.Interfaces.CancelOrderCallBack;
import com.freshfarm.freshfarmnew.Model.OrderListModel;
import com.freshfarm.freshfarmnew.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyOrdersFragment extends Fragment {

    List<OrderListModel> list = new ArrayList<>();
    String url = "", cus_id = "";
    private RecyclerView recyclerView;
    private ProgressBar progressbar;
    private LinearLayout nonemptyMyOrders;
    private ConstraintLayout emptyMyOrders;
    private Button btnExplore;
    MyOrdersAdapter myOrdersAdapter;

    public MyOrdersFragment() {
        // Required empty public constructor
    }

    public static MyOrdersFragment newInstance(String param1, String param2) {
        MyOrdersFragment fragment = new MyOrdersFragment();
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
        View v = inflater.inflate(R.layout.fragment_my_orders, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        progressbar = v.findViewById(R.id.progressbar);
        emptyMyOrders = v.findViewById(R.id.emptyMyOrders);
        nonemptyMyOrders = v.findViewById(R.id.nonemptyMyOrders);
        btnExplore = v.findViewById(R.id.btnExplore);

        myOrdersAdapter = new MyOrdersAdapter(getContext(), list, new CancelOrderCallBack() {
            @Override
            public void cancelOrder(String orderId) {
                /*Bundle bundle = new Bundle();
                bundle.putString("OrderId", orderId);
                CancelOrderFragment cof = new CancelOrderFragment();
                cof.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("PlaceOrder");
                ft.replace(R.id.fragment_container, cof);
                ft.commit();*/

                new AlertDialog.Builder(getContext())
                        .setMessage("Are you sure you want to cancel your orders?")

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getCancelOrder(orderId);
                            }
                        })

                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }

            @Override
            public void orderDetails(String orderId) {
                Bundle bundle = new Bundle();
                bundle.putString("OrderId", orderId);
                OrderDetailsFragment odf = new OrderDetailsFragment();
                odf.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("PlaceOrder");
                ft.replace(R.id.fragment_container, odf);
                ft.commit();
            }
        });
        recyclerView.setAdapter(myOrdersAdapter);

        getMyOrderData();

        return v;
    }

    private void getCancelOrder(String orderId) {
        progressbar.setVisibility(View.VISIBLE);
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/cancelOrderDetail");
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("PrintLog", "----response----" + response);
                        progressbar.setVisibility(View.GONE);
                        BaseUrl b = new BaseUrl();
                        url = b.url;
                        if (response != null) {
                            JSONObject json = null;
                            try {
                                json = new JSONObject(String.valueOf(response));
                                JSONObject json2 = json.getJSONObject("cancelOrderDetail");
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                Log.e("PrintLog", "--dataStr--" + stat);
                                if (stat.equals("true")) {

                                    getMyOrderData();

                                    String msg = json2.getString("Message");
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
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

    private void getMyOrderData() {
        progressbar.setVisibility(View.VISIBLE);
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/getOrderHistory");
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressbar.setVisibility(View.GONE);
                        //Log.e("PrintLog", "----" + response);
                        BaseUrl b = new BaseUrl();
                        url = b.url;
                        if (response != null) {
                            JSONObject json = null;

                            try {
                                json = new JSONObject(String.valueOf(response));
                                JSONObject json2 = json.getJSONObject("getOrderHistory");
                                Boolean status = json2.getBoolean("Status");
                                String stat = status.toString();
                                if (stat.equals("true")) {
                                    list.clear();

                                    JSONArray data = json2.getJSONArray("data");
                                    String dataStr = data.toString();
                                    //Log.e("PrintLog", "--dataStr--" + dataStr);
                                    Gson gson = new Gson();

                                    Type cartListType = new TypeToken<List<OrderListModel>>() {
                                    }.getType();

                                    list.addAll(gson.fromJson(dataStr, cartListType));

                                    //Log.e("PrintLog", "--list--" + list.size());

                                    if (list.size() > 0) {
                                        nonemptyMyOrders.setVisibility(View.VISIBLE);
                                        emptyMyOrders.setVisibility(View.GONE);

                                        myOrdersAdapter.notifyDataSetChanged();

                                    } else {
                                        emptyMyOrders.setVisibility(View.VISIBLE);
                                        nonemptyMyOrders.setVisibility(View.GONE);
                                    }

                                    //Toast.makeText(getContext(), "" + userArray.size(), Toast.LENGTH_LONG).show();

                                } else if (stat.equals("false")) {
                                    emptyMyOrders.setVisibility(View.VISIBLE);
                                    nonemptyMyOrders.setVisibility(View.GONE);

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