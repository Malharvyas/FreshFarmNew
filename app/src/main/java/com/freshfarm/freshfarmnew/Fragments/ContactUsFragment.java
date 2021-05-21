package com.freshfarm.freshfarmnew.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.freshfarm.freshfarmnew.Class.BaseUrl;
import com.freshfarm.freshfarmnew.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ContactUsFragment extends Fragment {

    String url = "", cus_name = "", contact = "", email = "", msg = "";
    private EditText c_name, c_mob, c_email, c_message;
    private Button send_message;

    public ContactUsFragment() {
        // Required empty public constructor
    }

    public static ContactUsFragment newInstance(String param1, String param2) {
        ContactUsFragment fragment = new ContactUsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact_us, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        c_name = v.findViewById(R.id.c_name);
        c_mob = v.findViewById(R.id.c_mob);
        c_email = v.findViewById(R.id.c_email);
        c_message = v.findViewById(R.id.c_message);
        send_message = v.findViewById(R.id.send_message);

        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cus_name = c_name.getText().toString();
                contact = c_mob.getText().toString();
                email = c_email.getText().toString();
                msg = c_message.getText().toString();

                if (cus_name.isEmpty()) {
                    Toast.makeText(getContext(), "Please Enter Name.", Toast.LENGTH_LONG).show();
                } else if (contact.isEmpty()) {
                    Toast.makeText(getContext(), "Please Enter Contact.", Toast.LENGTH_LONG).show();
                } else if (email.isEmpty()) {
                    Toast.makeText(getContext(), "Please Enter Email.", Toast.LENGTH_LONG).show();
                } else if (msg.isEmpty()) {
                    Toast.makeText(getContext(), "Please Enter Message.", Toast.LENGTH_LONG).show();
                } else {
                    getContactDetails(cus_name, contact, email, msg);
                }
            }
        });

        return v;
    }

    private void getContactDetails(String cus_name, String contact, String email, String msg) {
        this.cus_name = c_name.getText().toString();
        this.contact = c_mob.getText().toString();
        this.email = c_email.getText().toString();
        this.msg = c_message.getText().toString();

        //progressbar.setVisibility(View.VISIBLE);
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/createCustomerQuery");
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
                                JSONObject json2 = json.getJSONObject("createCustomerQuery");
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                if (stat.equals("true")) {

                                    String msg = json2.getString("Message");
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

                                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav);
                                    bottomNavigationView.getMenu().getItem(0).setChecked(true);
                                    HomeFragment h = new HomeFragment();
                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("home");
                                    ft.replace(R.id.fragment_container, h);
                                    ft.commit();
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
                //progressbar.setVisibility(View.GONE);
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
                params.put("customer_name", cus_name);
                params.put("contact_no", contact);
                params.put("email", email);
                params.put("message", msg);
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