package com.example.freshfarmnew.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.freshfarmnew.Class.BaseUrl;
import com.example.freshfarmnew.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class PlaceOrderFragment extends Fragment implements View.OnClickListener {

    CardView card_wallet, card_online, card_cash;
    ImageView icon_wallet, icon_online, icon_cash;
    TextView text_wallet1, text_wallet2, text_online, text_cash;
    EditText promo_code;
    TextView apply_promo, promo_warning, delivery_charges, discount, net_amount;
    String url = "", cus_id = "";
    ProgressBar progressBar;
    private TextView billed_amount;
    private List<String> pidlist;
    List<String> pquantlist;
    List<String> ppricelist;
    private String addressId = "", total_amount = "", delivery_date = "";
    Button place_order;
    int deleviry, discount2;
    Double netamount;

    public PlaceOrderFragment() {
        // Required empty public constructor
    }

    public static PlaceOrderFragment newInstance(String param1, String param2) {
        PlaceOrderFragment fragment = new PlaceOrderFragment();
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
        View v = inflater.inflate(R.layout.fragment_place_order, container, false);
        card_wallet = v.findViewById(R.id.card4);
        icon_wallet = v.findViewById(R.id.icon1);
        text_wallet1 = v.findViewById(R.id.text6);
        text_wallet2 = v.findViewById(R.id.wallet_amount);

        card_cash = v.findViewById(R.id.card6);
        icon_cash = v.findViewById(R.id.icon3);
        text_cash = v.findViewById(R.id.text8);

        card_online = v.findViewById(R.id.card5);
        icon_online = v.findViewById(R.id.icon2);
        text_online = v.findViewById(R.id.text7);

        promo_code = v.findViewById(R.id.promo_code);
        apply_promo = v.findViewById(R.id.apply_promo);
        promo_warning = v.findViewById(R.id.promo_warning);

        progressBar = v.findViewById(R.id.progressbar);

        card_wallet.setOnClickListener(this);
        card_cash.setOnClickListener(this);
        card_online.setOnClickListener(this);

        billed_amount = v.findViewById(R.id.billed_amount);

        place_order = v.findViewById(R.id.place_order);

        delivery_charges = v.findViewById(R.id.delivery_charges);
        discount = v.findViewById(R.id.discount);
        net_amount = v.findViewById(R.id.net_amount);


        if (getArguments() != null) {
            if (getArguments().containsKey("amount")) {
                billed_amount.setText(getArguments().getString("amount"));
                deleviry = 11;
                discount2 = 0;
                printreceipt(deleviry, discount2);
            }
            if (getArguments().containsKey("addressId")) {
                addressId = getArguments().getString("addressId");
            }
        }

        apply_promo.setOnClickListener(this);
        place_order.setOnClickListener(this);

        promo_code.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    promo_warning.setVisibility(View.GONE);
                }
            }
        });

        promo_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                apply_promo.setText("Apply");
            }
        });
        return v;
    }

    private void printreceipt(int deleviry, int discount2) {
        netamount = Double.parseDouble(billed_amount.getText().toString()) + Double.parseDouble(String.valueOf(deleviry)) + Double.parseDouble(String.valueOf(discount2));
        delivery_charges.setText("" + deleviry);
        discount.setText("" + discount2);
        net_amount.setText("" + netamount);
    }

//    private void printreceipt(int deleviry, int discount, Double netamount) {
//
//        this.discount.setText("0");
//        net_amount.setText(""+net);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card4: {
                card_cash.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                DrawableCompat.setTint(
                        DrawableCompat.wrap(icon_cash.getDrawable()),
                        ContextCompat.getColor(getContext(), R.color.black)
                );
                text_cash.setTextColor(Color.parseColor("#757575"));

                card_online.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                DrawableCompat.setTint(
                        DrawableCompat.wrap(icon_online.getDrawable()),
                        ContextCompat.getColor(getContext(), R.color.black)
                );
                text_online.setTextColor(Color.parseColor("#757575"));

                card_wallet.setCardBackgroundColor(Color.parseColor("#FFA823"));
                DrawableCompat.setTint(
                        DrawableCompat.wrap(icon_wallet.getDrawable()),
                        ContextCompat.getColor(getContext(), R.color.white)
                );
                text_wallet1.setTextColor(Color.parseColor("#FFFFFF"));
                text_wallet2.setTextColor(Color.parseColor("#FFFFFF"));
            }
            break;
            case R.id.card6: {
                card_wallet.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                DrawableCompat.setTint(
                        DrawableCompat.wrap(icon_wallet.getDrawable()),
                        ContextCompat.getColor(getContext(), R.color.black)
                );
                text_wallet1.setTextColor(Color.parseColor("#757575"));
                text_wallet2.setTextColor(Color.parseColor("#757575"));

                card_online.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                DrawableCompat.setTint(
                        DrawableCompat.wrap(icon_online.getDrawable()),
                        ContextCompat.getColor(getContext(), R.color.black)
                );
                text_online.setTextColor(Color.parseColor("#757575"));

                card_cash.setCardBackgroundColor(Color.parseColor("#FFA823"));
                DrawableCompat.setTint(
                        DrawableCompat.wrap(icon_cash.getDrawable()),
                        ContextCompat.getColor(getContext(), R.color.white)
                );
                text_cash.setTextColor(Color.parseColor("#FFFFFF"));
            }
            break;
            case R.id.card5: {
                card_cash.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                DrawableCompat.setTint(
                        DrawableCompat.wrap(icon_cash.getDrawable()),
                        ContextCompat.getColor(getContext(), R.color.black)
                );
                text_cash.setTextColor(Color.parseColor("#757575"));

                card_wallet.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                DrawableCompat.setTint(
                        DrawableCompat.wrap(icon_wallet.getDrawable()),
                        ContextCompat.getColor(getContext(), R.color.black)
                );
                text_wallet1.setTextColor(Color.parseColor("#757575"));
                text_wallet2.setTextColor(Color.parseColor("#757575"));

                card_online.setCardBackgroundColor(Color.parseColor("#FFA823"));
                DrawableCompat.setTint(
                        DrawableCompat.wrap(icon_online.getDrawable()),
                        ContextCompat.getColor(getContext(), R.color.white)
                );
                text_online.setTextColor(Color.parseColor("#FFFFFF"));
            }
            break;
            case R.id.apply_promo: {
                String promo = promo_code.getText().toString();
                if (promo == null || promo.equals("")) {
                    Toast.makeText(getContext(), "Invalid promocode", Toast.LENGTH_SHORT).show();
                } else {
                    verifypromo(cus_id, promo);
                }
            }
            break;
            case R.id.place_order: {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("cartdetails", Context.MODE_PRIVATE);
                String total_amount = net_amount.getText().toString();


//                Log.e("PrintLog", "----" + pidlist.size());

//                Toast.makeText(getContext(),""+delivery_date,Toast.LENGTH_SHORT).show();

                if (total_amount == null || total_amount.equals("")) {
                    Toast.makeText(getContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                } else {
                    makepayment(total_amount);
                }
            }
            break;
        }
    }

    private void makepayment(String total_amount) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userpref", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("customer_name", "");
        String useremail = sharedPreferences.getString("customer_email", "");
        String usermobile = sharedPreferences.getString("customer_phone", "");

        Checkout checkout = new Checkout();

        checkout.setKeyID("rzp_test_q3dmr1uvAp87kX");

        checkout.setImage(R.drawable.logo);

        try {
            JSONObject options = new JSONObject();

            options.put("name", username);
            options.put("currency", "INR");
            options.put("prefill.email", useremail);
            options.put("prefill.contact", "+91" + usermobile);
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            double total = Double.parseDouble(total_amount);
            total = total * 100;
            options.put("amount", total);

            checkout.open(getActivity(), options);

        } catch (Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }

    private void placeorder(String cus_id, String addressId, String total_amount, List<String> pidlist, List<String> pquantlist, List<String> ppricelist, String delivery_date) {
        progressBar.setVisibility(View.VISIBLE);
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/createOrder");
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        BaseUrl b = new BaseUrl();
                        url = b.url;
                        if (response != null) {
                            progressBar.setVisibility(View.GONE);
                            JSONObject json = null;

                            try {
                                json = new JSONObject(String.valueOf(response));
                                JSONObject json2 = json.getJSONObject("createOrder");
                                Boolean status = json2.getBoolean("Status");
                                String stat = status.toString();
                                if (stat.equals("true")) {
                                    SharedPreferences sh1 = getActivity().getSharedPreferences("payment_details",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sh1.edit();
                                    editor.clear();
                                    editor.apply();

                                    SharedPreferences sh2 = getActivity().getSharedPreferences("cartdetails",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = sh2.edit();
                                    editor2.clear();
                                    editor2.apply();

                                    String msg = json2.getString("Message");
                                    Toast.makeText(getContext(), "" + msg, Toast.LENGTH_SHORT).show();

                                } else if (stat.equals("false")) {
                                    String msg = json2.getString("Message");
                                    Toast.makeText(getContext(), "" + msg, Toast.LENGTH_SHORT).show();
                                }
//                                Toast.makeText(getContext(),""+catlist.size(),Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
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
                params.put("address_id", addressId);
                params.put("payment_id", "1");
                params.put("delivery_charge", delivery_charges.getText().toString());
                params.put("scheduled_date", delivery_date);
                params.put("delivery_time_id", "1");
                params.put("total_amount", total_amount);
                params.put("payment_status", "1");
                for (int i = 0; i < pidlist.size(); i++) {
                    params.put("product_data[" + i + "][quantity]", pquantlist.get(i));
                    params.put("product_data[" + i + "][price]", ppricelist.get(i));
                    params.put("product_data[" + i + "][product_id]", pidlist.get(i));

                    Log.e("PrintLog", "----" + pidlist.get(i));
                }
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

    private void verifypromo(String cus_id, String promo) {
        progressBar.setVisibility(View.VISIBLE);
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/verifyPromo");
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        BaseUrl b = new BaseUrl();
                        url = b.url;
                        if (response != null) {
                            progressBar.setVisibility(View.GONE);
                            JSONObject json = null;

                            try {
                                json = new JSONObject(String.valueOf(response));
                                JSONObject json2 = json.getJSONObject("removeFromWishList");
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                if (stat.equals("true")) {
                                    String msg = json2.getString("Message");
                                    promo_code.clearFocus();
                                    Toast.makeText(getContext(), "Code Applied", Toast.LENGTH_SHORT).show();
                                    apply_promo.setText("Applied");

                                } else if (stat.equals("false")) {
                                    promo_code.clearFocus();
                                    promo_warning.setVisibility(View.VISIBLE);
                                }
//                                Toast.makeText(getContext(),""+catlist.size(),Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
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
                params.put("promocode", promo);
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

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("payment_details", Context.MODE_PRIVATE);
        String paymentdone = sharedPreferences.getString("cart_payment", "");
        String status = sharedPreferences.getString("cpstatus", "");

        if (paymentdone.equals("1")) {
            if (status.equals("1")) {
                SharedPreferences sharedPreferences2 = getActivity().getSharedPreferences("cartdetails", Context.MODE_PRIVATE);
                String pidset = sharedPreferences2.getString("pidset", null);
                String pquantset = sharedPreferences2.getString("pquantset", null);
                String ppriceset = sharedPreferences2.getString("ppriceset", null);

                Gson gson = new Gson();
                List<String> pidlist = null, pquantlist = null, ppricelist = null;
                if (pidset != null) {
                    pidlist = gson.fromJson(pidset, new TypeToken<List<String>>() {
                    }.getType());
                }
                if (pquantset != null) {
                    pquantlist = gson.fromJson(pquantset, new TypeToken<List<String>>() {
                    }.getType());
                }
                if (ppriceset != null) {
                    ppricelist = gson.fromJson(ppriceset, new TypeToken<List<String>>() {
                    }.getType());
                }

                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                String formattedDate = df.format(c);

                Calendar c1 = Calendar.getInstance();
                try {
                    c1.setTime(df.parse(formattedDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                c1.add(Calendar.DATE, 2);

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date result = new Date(c1.getTimeInMillis());
                String delivery_date = sdf.format(result);
                String total_amount = net_amount.getText().toString();
                placeorder(cus_id, addressId, total_amount, pidlist, pquantlist, ppricelist, delivery_date);
            }
        }
    }
}