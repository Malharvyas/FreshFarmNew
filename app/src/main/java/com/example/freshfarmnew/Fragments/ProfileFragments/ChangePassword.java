package com.example.freshfarmnew.Fragments.ProfileFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.freshfarmnew.Class.BaseUrl;
import com.example.freshfarmnew.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ChangePassword extends Fragment {

    EditText current_pass,new_pass,conf_pass;
    String cur_p,new_p,conf_p,cus_id;
    String url = "";
    Button save;
    int check = 0;

    public ChangePassword() {
        // Required empty public constructor
    }

    public static ChangePassword newInstance(String param1, String param2) {
        ChangePassword fragment = new ChangePassword();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_password, container, false);
        current_pass = v.findViewById(R.id.current_pass);
        new_pass = v.findViewById(R.id.new_pass);
        conf_pass = v.findViewById(R.id.confirm_pass);
        save = v.findViewById(R.id.save_pass);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userpref", Context.MODE_PRIVATE);
        cus_id = sharedPreferences.getString("customer_id","");

        conf_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pass1 = new_pass.getText().toString();
                String pass2 = conf_pass.getText().toString();
                if(pass2.equals(pass1))
                {
                    check = 1;
                }
                else
                {
                    check = 0;
                    conf_pass.setError("Password does not matches");
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cur_p = current_pass.getText().toString();
                new_p = new_pass.getText().toString();
                conf_p = conf_pass.getText().toString();

                if(check == 1)
                {
                    changepass(cus_id,cur_p,new_p,conf_p);
                }
                else
                {
                    Toast.makeText(getContext(),"Please correct the error",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }

    private void changepass(String cus_id, String cur_p, String new_p, String conf_p) {
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/resetpass");
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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

                                    Toast.makeText(getContext(),""+msg,Toast.LENGTH_SHORT).show();
                                    getActivity().getSupportFragmentManager().popBackStack();
                                }
                                else if(stat.equals("false"))
                                {
                                    String msg = json2.getString("Message");
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
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
                            Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(getContext(),"Error : "+error,Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("customer_id",cus_id);
                params.put("password",new_p);
                params.put("confirm_password",conf_p);
                params.put("old_password",cur_p);
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