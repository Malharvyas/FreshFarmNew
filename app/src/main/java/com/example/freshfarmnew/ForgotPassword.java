package com.example.freshfarmnew;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.freshfarmnew.Class.BaseUrl;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    EditText f_email,f_otp,f_pass1,f_pass2;
    Button f_send,f_verify,f_change;
    String email,upass1,upass2;
    String url = "";
    ConstraintLayout otp_layot,reset_pass;
    int check = 0, check2 = 0;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        f_email = findViewById(R.id.f_email);
        f_send = findViewById(R.id.f_send);
        otp_layot = findViewById(R.id.getotp);
        reset_pass = findViewById(R.id.reset);
        f_otp = otp_layot.findViewById(R.id.f_otp);
        f_verify = otp_layot.findViewById(R.id.f_verify);
        f_pass1 = reset_pass.findViewById(R.id.f_pass1);
        f_pass2 = reset_pass.findViewById(R.id.f_pass2);
        f_change = reset_pass.findViewById(R.id.f_change);
        progressBar = findViewById(R.id.progressbar);


        f_send.setOnClickListener(this);
        f_verify.setOnClickListener(this);
        f_change.setOnClickListener(this);

        f_pass2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pass1 = f_pass1.getText().toString();
                String pass2 = f_pass2.getText().toString();
                if(pass2.equals(pass1))
                {
                    check2 = 1;
                }
                else
                {
                    check2 = 0;
                    f_pass2.setError("Password does not matches");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.f_send:
            {
                email = f_email.getText().toString();
                sendverfication(email);
            }
            break;
            case R.id.f_verify:
            {
                SharedPreferences sharedPreferences = getSharedPreferences("forgot_otp", Context.MODE_PRIVATE);
                String otp = sharedPreferences.getString("otp","0");
                String uotp = f_otp.getText().toString();
                if(otp.equals(uotp))
                {
                    reset_pass.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(),"Verified...You can change your password",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Please provide the correct otp",Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case R.id.f_change:
            {
                SharedPreferences sharedPreferences = getSharedPreferences("forgot_otp", Context.MODE_PRIVATE);
                String c_id = sharedPreferences.getString("customer_id","0");
                checkdata();
                upass1 = f_pass1.getText().toString();
                upass2 = f_pass2.getText().toString();
                if(check == 1)
                {
                    if(check2 == 1)
                    {
                        sendtoapi(upass1,upass2,c_id);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Please correct the errors",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please correct the errors",Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    private void sendtoapi(String upass1, String upass2, String c_id) {
        progressBar.setVisibility(View.VISIBLE);
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/resetPassword");
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        BaseUrl b = new BaseUrl();
                        url = b.url;
                        if(response != null) {
                            JSONObject json = null;

                            try {
                                json = new JSONObject(String.valueOf(response));
                                JSONObject json2 = json.getJSONObject("resetPassword");
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                if(stat.equals("true"))
                                {
                                    String msg = json2.getString("Message");
                                    Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(),login.class));
                                    finish();
                                }
                                else if(stat.equals("false")){
                                    String msg = json2.getString("Message");
                                    Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
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
                if(error instanceof ClientError)
                {
                    try{
                        String responsebody = new String(error.networkResponse.data,"utf-8");
                        JSONObject data = new JSONObject(responsebody);
                        Boolean status = data.getBoolean("status");
                        String stat = status.toString();
                        if(stat.equals("false"))
                        {
                            String msg = data.getString("Message");
                            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Error : "+error,Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("password",upass1);
                params.put("confirm_password",upass2);
                params.put("customer_id",c_id);
                return params;
            }
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
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

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    private void checkdata() {
        if(isEmpty(f_pass1))
        {
            f_pass1.setError("Password cannot be empty");
        }
        else {
            check = 1;
        }
    }

    private void sendverfication(String email) {
        progressBar.setVisibility(View.VISIBLE);
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/forgotPassword");
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        BaseUrl b = new BaseUrl();
                        url = b.url;
                        if(response != null) {
                            JSONObject json = null;

                            try {
                                json = new JSONObject(String.valueOf(response));
                                JSONObject json2 = json.getJSONObject("forgotPassword");
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                if(stat.equals("true"))
                                {
                                    String msg = json2.getString("Message");
                                    Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                                    JSONObject data = json2.getJSONObject("data");
                                    String otp = data.getString("otp");
                                    String customer_id = data.getString("customer_id");
                                    SharedPreferences sharedPreferences = getSharedPreferences("forgot_otp", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("otp",otp);
                                    editor.putString("customer_id",customer_id);
                                    editor.apply();
                                    otp_layot.setVisibility(View.GONE);
                                    reset_pass.setVisibility(View.GONE);
                                    otp_layot.setVisibility(View.VISIBLE);
                                }
                                else if(stat.equals("false"))
                                {
                                    String msg = json2.getString("Message");
                                    Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
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
                if(error instanceof ClientError)
                {
                    try{
                        String responsebody = new String(error.networkResponse.data,"utf-8");
                        JSONObject data = new JSONObject(responsebody);
                        Boolean status = data.getBoolean("status");
                        String stat = status.toString();
                        if(stat.equals("false"))
                        {
                            String msg = data.getString("message");
                            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Error : "+error,Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("customer_email",email);
                return params;
            }
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
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