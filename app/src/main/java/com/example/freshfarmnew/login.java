package com.example.freshfarmnew;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity implements View.OnClickListener {

    Button nav_signup, login;
    EditText l_mob;
    TextInputEditText lpass;
    String mob, pass;
    int check = 0;
    String url = "";
    TextView forgot_pass;
    ProgressBar progressBar;
    TextInputLayout passlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nav_signup = findViewById(R.id.nav_signup);
        l_mob = findViewById(R.id.l_mob_number);
        lpass = findViewById(R.id.l_pass);
        login = findViewById(R.id.login);
        forgot_pass = findViewById(R.id.forgot_pass);
        progressBar = findViewById(R.id.progressbar);
        passlayout = findViewById(R.id.pass_layout);

        nav_signup.setOnClickListener(this);
        login.setOnClickListener(this);
        forgot_pass.setOnClickListener(this);

        lpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                lpass.setBackgroundResource(R.drawable.border_edittext);
                passlayout.setError(null);
            }
        });
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    boolean isEmpty(TextInputEditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_signup: {
                Intent i = new Intent(getApplicationContext(), Register.class);
                startActivity(i);
            }
            break;
            case R.id.login: {
                checkdata();
                if (check == 1) {
                    mob = l_mob.getText().toString();
                    pass = lpass.getText().toString();
                    loginwithcredentials(mob, pass);
                } else {
                    Toast.makeText(getApplicationContext(), "Please correct the errors first!!", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case R.id.forgot_pass: {
                startActivity(new Intent(getApplicationContext(), ForgotPassword.class));
            }
            break;
        }
    }

    private void loginwithcredentials(String mob, String pass) {
        progressBar.setVisibility(View.VISIBLE);
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/signin");
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("printLog", "----signin----" + response);
                        progressBar.setVisibility(View.GONE);
                        BaseUrl b = new BaseUrl();
                        url = b.url;
                        if (response != null) {
                            JSONObject json = null;

                            try {
                                json = new JSONObject(String.valueOf(response));
                                JSONObject json2 = json.getJSONObject("SignIn");
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                if (stat.equals("true")) {
                                    JSONArray data = json2.getJSONArray("data");
                                    JSONObject jsonObject = data.getJSONObject(0);
                                    String customer_id = jsonObject.getString("customer_id");
                                    String customer_name = jsonObject.getString("customer_name");
                                    String customer_phone = jsonObject.getString("customer_phone");
                                    String customer_email = jsonObject.getString("customer_email");
                                    String device_uid = jsonObject.getString("device_uid");
                                    String device_name = jsonObject.getString("device_name");
                                    String login_token = jsonObject.getString("login_token");
                                    String device_type = jsonObject.getString("device_type");
                                    String os_version = jsonObject.getString("os_version");
                                    String address = jsonObject.getString("address");
                                    String ref_code = jsonObject.getString("ref_code");
                                    String mobile_verify = jsonObject.getString("mobile_verify");

                                    Log.e("printLog", "----login_token----" + login_token);

                                    SharedPreferences sharedPreferences = getSharedPreferences("userpref", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("customer_id", customer_id);
                                    editor.putString("customer_name", customer_name);
                                    editor.putString("customer_phone", customer_phone);
                                    editor.putString("customer_email", customer_email);
                                    editor.putString("device_uid", device_uid);
                                    editor.putString("device_name", device_name);
                                    editor.putString("device_type", device_type);
                                    editor.putString("os_version", os_version);
                                    editor.putString("address", address);
                                    editor.putString("ref_code", ref_code);
                                    editor.apply();

                                    if (mobile_verify.equals("0")) {

                                        new AlertDialog.Builder(login.this)
                                                .setMessage("Please verify your number")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        sendotp(customer_phone, login_token);

                                                    }
                                                })
                                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();
                                    } else {

                                        SharedPreferences sharedPreferences1 = getSharedPreferences("userlogin", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                                        editor1.putBoolean("islogin", true);
                                        editor1.apply();

                                        Toast.makeText(getApplicationContext(), "Login Successfull", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }

                                } else if (stat.equals("false")) {
                                    String msg = json2.getString("Message");
//                                    Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                                    passlayout.setError(msg);
                                    lpass.setBackgroundResource(R.drawable.border_edittext);
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
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error : " + error, Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("customer_phone", mob);
                params.put("password", pass);
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

    private void sendotp(String umob, String login_token) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupOTPView = inflater.inflate(R.layout.otp_popup, null);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(login.this);
        alertDialogBuilder.setView(popupOTPView);
        alertDialogBuilder.setCancelable(false);
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.getWindow().setGravity(Gravity.TOP);
        EditText otp = popupOTPView.findViewById(R.id.reg_otp);
        Button verify = popupOTPView.findViewById(R.id.reg_verify_otp);
        TextView resend = popupOTPView.findViewById(R.id.resend_otp);
        otp.setText(login_token);
        otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp.setError(null);
            }
        });
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userotp = otp.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                BaseUrl b = new BaseUrl();
                url = b.url;
                url = url.concat("freshfarm/api/ApiController/checkOtp");
                RequestQueue volleyRequestQueue = Volley.newRequestQueue(getApplicationContext());

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("printLog", "===checkOtp===" + response);
                                progressBar.setVisibility(View.GONE);
                                BaseUrl b = new BaseUrl();
                                url = b.url;
                                if (response != null) {
                                    JSONObject json = null;

                                    try {
                                        json = new JSONObject(String.valueOf(response));
                                        JSONObject json2 = json.getJSONObject("checkOtp");
                                        Boolean status = json2.getBoolean("status");
                                        String stat = status.toString();
                                        if (stat.equals("true")) {
                                            String msg = json2.getString("Message");

                                            SharedPreferences sharedPreferences1 = getSharedPreferences("userlogin", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                                            editor1.putBoolean("islogin", true);
                                            editor1.apply();

                                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else if (stat.equals("false")) {
                                            String msg = json2.getString("Message");
//                                            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                                            otp.setError(msg);
                                            otp.setBackgroundResource(R.drawable.border_edittext);
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
                                    String msg = data.getString("message");
                                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error : " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("customer_phone", umob);
                        params.put("login_token", userotp);
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
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userotp = otp.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                BaseUrl b = new BaseUrl();
                url = b.url;
                url = url.concat("freshfarm/api/ApiController/resendOtp");
                RequestQueue volleyRequestQueue = Volley.newRequestQueue(getApplicationContext());

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("printLog", "===resendOtp===" + response);
                                progressBar.setVisibility(View.GONE);
                                BaseUrl b = new BaseUrl();
                                url = b.url;
                                if (response != null) {
                                    JSONObject json = null;

                                    try {
                                        json = new JSONObject(String.valueOf(response));
                                        JSONObject json2 = json.getJSONObject("resendOtp");
                                        Boolean status = json2.getBoolean("status");
                                        String stat = status.toString();
                                        if (stat.equals("true")) {
                                            JSONObject data = json2.getJSONObject("data");
                                            String login_token = data.getString("login_token");

                                            Log.e("printLog", "===login_token_Resend===" + login_token);

                                            otp.setText(login_token);
                                            Toast.makeText(getApplicationContext(), "OTP sent successfully", Toast.LENGTH_LONG).show();
                                        } else if (stat.equals("false")) {
                                            String msg = json2.getString("Message");
//                                            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                                            otp.setError(msg);
                                            otp.setBackgroundResource(R.drawable.border_edittext);
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
                                    String msg = data.getString("message");
                                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error : " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("customer_phone", umob);
                        params.put("login_token", userotp);
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
        });
    }

    private void checkdata() {
        if (isEmpty(l_mob)) {
            l_mob.setError("Mobile Number cannot be empty");
        } else if (isEmpty(lpass)) {
            lpass.setError("Password field cannot be empty");
        } else {
            check = 1;
        }
    }
}