package com.example.freshfarmnew;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity implements View.OnClickListener {

    String url = "";
    TextView textView;
    Button nav_login,sign_up;
    CheckBox term_policy;
    EditText username,mob_number,useremail,referral;
    TextInputEditText userpass1,userpass2;
    String uname,umob,uemail,upass1,upass2,uref;
    String device_id,device_name,device_type,device_os;
    int check = 0, check2 = 0,check3 = 0;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        textView = findViewById(R.id.text3);
        nav_login = findViewById(R.id.nav_login);
        username = findViewById(R.id.username);
        mob_number = findViewById(R.id.mob_number);
        useremail = findViewById(R.id.useremail);
        userpass1 = findViewById(R.id.userpass1);
        userpass2 = findViewById(R.id.userpass2);
        referral = findViewById(R.id.referral);
        sign_up = findViewById(R.id.sign_up);
        term_policy = findViewById(R.id.term_policy_check);
        progressBar = findViewById(R.id.progressbar);

        useremail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = useremail.getText().toString();
                if(email.matches(emailPattern))
                {
                    check2 = 1;
                }
                else
                {
                    check2 = 0;
                    useremail.setError("Please enter a valid email");
                }
            }
        });

        userpass2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pass1 = userpass1.getText().toString();
                String pass2 = userpass2.getText().toString();
                if(pass2.equals(pass1))
                {
                    check3 = 1;
                }
                else
                {
                    check3 = 0;
                    userpass2.setError("Password does not matches");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        nav_login.setOnClickListener(this);
        sign_up.setOnClickListener(this);

        String s = "By Registering,You Agree To The Terms & Privacy Policy";

        SpannableString myString = new SpannableString(s);
        String first = "Terms";
        String second = "Privacy Policy";
        int firstIndex = s.indexOf(first);
        int secondIndex = s.indexOf(second);

        ClickableSpan firstwordClick = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(getApplicationContext(),Terms.class));
            }
        };
        ClickableSpan secondwordClick = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(getApplicationContext(),PrivacyPolicy.class));
            }
        };

        myString.setSpan(firstwordClick,firstIndex, firstIndex+first.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        myString.setSpan(secondwordClick,secondIndex, secondIndex+second.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setLinksClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(myString, TextView.BufferType.SPANNABLE);

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
        switch (v.getId())
        {
            case R.id.nav_login:
            {
//                Intent i = new Intent(getApplicationContext(),login.class);
//                startActivity(i);
                sendotp("5522");
            }
            break;
            case R.id.sign_up:
            {
                checkdata();
                if(check == 1)
                {
                    if(check2 == 1)
                    {
                        if(check3 == 1)
                        {
                            if(term_policy.isChecked())
                            {
                                uname = username.getText().toString();
                                umob = mob_number.getText().toString();
                                uemail = useremail.getText().toString();
                                upass1 = userpass1.getText().toString();
                                upass2 = userpass2.getText().toString();
                                uref = referral.getText().toString();
                                device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                                device_name = Build.MODEL;
                                device_type = "Android";
                                device_os = String.valueOf(Build.VERSION.SDK_INT);
                                senddatatoapi(uname,umob,uemail,upass1,upass2,uref,device_id,device_name,device_type,device_os);
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Please accept the terms and privacy policy",Toast.LENGTH_SHORT).show();
                            }

                        }
                        else{
                            Toast.makeText(getApplicationContext(),"You cannot register without correcting errors",Toast.LENGTH_SHORT).show();
                        }

                    }
                    else{
                        Toast.makeText(getApplicationContext(),"You cannot register without correcting errors",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"You cannot register without correcting errors",Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    private void senddatatoapi(String uname, String umob, String uemail, String upass1,String upass2, String uref, String device_id, String device_name, String device_type, String device_os) {
        progressBar.setVisibility(View.VISIBLE);
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/signup");
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
                                JSONObject json2 = json.getJSONObject("userSignup");
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                if(stat.equals("true"))
                                {
                                    String msg = json2.getString("Message");
                                    Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
//                                    Intent i = new Intent(getApplicationContext(),login.class);
//                                    startActivity(i);
//                                    finish();
                                    sendotp(umob);
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
                params.put("customer_name",uname);
                params.put("customer_email",uemail);
                params.put("customer_phone",umob);
                params.put("password",upass1);
                params.put("passconf",upass2);
                params.put("device_uid",device_id);
                params.put("device_name",device_name);
                params.put("device_type",device_type);
                params.put("os_version",device_os);
                return params;
            }
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError{
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

    private void sendotp(String umob) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupOTPView = inflater.inflate(R.layout.otp_popup, null);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Register.this);
        alertDialogBuilder.setView(popupOTPView);
        alertDialogBuilder.setCancelable(false);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.getWindow().setGravity(Gravity.TOP);
        EditText otp = popupOTPView.findViewById(R.id.reg_otp);
        Button verify = popupOTPView.findViewById(R.id.reg_verify_otp);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userotp = otp.getText().toString();
            }
        });
    }

    private void checkdata() {
        if(isEmpty(username))
        {
            check = 0;
            username.setError("Username cannot be empty");
        }
        else if(isEmpty(useremail))
        {
            check = 0;
            useremail.setError("Email cannot be empty");
        }
        else if(isEmpty(mob_number))
        {
            check = 0;
            mob_number.setError("Mobile Number cannot be empty");
        }
        else if(isEmpty(userpass1))
        {
            check = 0 ;
            userpass1.setError("Password cannot be empty");
        }
        else {
            check = 1;
        }
    }
}