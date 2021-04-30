package com.example.freshfarmnew;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.freshfarmnew.Model.Product;
import com.example.freshfarmnew.Model.ProductVariation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlaceOrderFragment extends Fragment implements View.OnClickListener {

    CardView card_wallet, card_online, card_cash;
    ImageView icon_wallet, icon_online, icon_cash;
    TextView text_wallet1, text_wallet2, text_online, text_cash;
    EditText promo_code;
    TextView apply_promo, promo_warning;
    String url = "", cus_id = "";
    ProgressBar progressBar;
    private TextView billed_amount;
    private String addressId = "";

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

        if (getArguments() != null) {
            if (getArguments().containsKey("amount")) {
                billed_amount.setText(getArguments().getString("amount"));
            }
            if (getArguments().containsKey("addressId")) {
                addressId = getArguments().getString("addressId");
            }
        }

        apply_promo.setOnClickListener(this);

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
        }
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
}