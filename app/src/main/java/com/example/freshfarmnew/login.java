package com.example.freshfarmnew;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity implements View.OnClickListener {

    Button nav_signup,login;
    EditText l_mob,lpass;
    String mob,pass;
    int check = 0;
    String url = "";
    TextView forgot_pass;
    ProgressBar progressBar;

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

        nav_signup.setOnClickListener(this);
        login.setOnClickListener(this);
        forgot_pass.setOnClickListener(this);

    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.nav_signup:
            {
                Intent i = new Intent(getApplicationContext(),Register.class);
                startActivity(i);
            }
            break;
            case R.id.login:
            {
                checkdata();
                if(check == 1)
                {
                    mob = l_mob.getText().toString();
                    pass = lpass.getText().toString();
                    loginwithcredentials(mob,pass);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please correct the errors first!!",Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case R.id.forgot_pass:
            {
                startActivity(new Intent(getApplicationContext(),ForgotPassword.class));
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
                        progressBar.setVisibility(View.GONE);
                        BaseUrl b = new BaseUrl();
                        url = b.url;
                        if(response != null) {
                            JSONObject json = null;

                            try {
                                json = new JSONObject(String.valueOf(response));
                                JSONObject json2 = json.getJSONObject("SignIn");
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                if(stat.equals("true"))
                                {
                                    JSONArray data = json2.getJSONArray("Message");
                                    JSONObject jsonObject = data.getJSONObject(0);
                                    String customer_id = jsonObject.getString("customer_id");
                                    String customer_name = jsonObject.getString("customer_name");
                                    String customer_phone = jsonObject.getString("customer_phone");
                                    String customer_email = jsonObject.getString("customer_email");
                                    String device_uid = jsonObject.getString("device_uid");
                                    String device_name = jsonObject.getString("device_name");
                                    String device_type = jsonObject.getString("device_type");
                                    String os_version = jsonObject.getString("os_version");
                                    String address = jsonObject.getString("address");

                                    SharedPreferences sharedPreferences = getSharedPreferences("userpref", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("customer_id",customer_id);
                                    editor.putString("customer_name",customer_name);
                                    editor.putString("customer_phone",customer_phone);
                                    editor.putString("customer_email",customer_email);
                                    editor.putString("device_uid",device_uid);
                                    editor.putString("device_name",device_name);
                                    editor.putString("device_type",device_type);
                                    editor.putString("os_version",os_version);
                                    editor.putString("address",address);
                                    editor.apply();

                                    SharedPreferences sharedPreferences1 = getSharedPreferences("userlogin",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                                    editor1.putBoolean("islogin",true);
                                    editor1.apply();

                                    Toast.makeText(getApplicationContext(),"Login Successfull",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();

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
                params.put("customer_phone",mob);
                params.put("password",pass);
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

    private void checkdata() {
        if(isEmpty(l_mob))
        {
            l_mob.setError("Mobile Number cannot be empty");
        }
        else if(isEmpty(lpass))
        {
            lpass.setError("Password field cannot be empty");
        }
        else{
            check = 1;
        }
    }
}