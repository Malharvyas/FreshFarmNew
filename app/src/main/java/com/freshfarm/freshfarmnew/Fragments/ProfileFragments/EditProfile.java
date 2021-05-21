package com.freshfarm.freshfarmnew.Fragments.ProfileFragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class EditProfile extends Fragment {

    EditText username,umobnum,uemailadd,uaddress;
    String uname,umob,uemail,uadd,cus_id;
    String url =  "";
    String updatedname,updatedadd;
    Button save;

    public EditProfile() {
        // Required empty public constructor
    }


    public static EditProfile newInstance(String param1, String param2) {
        EditProfile fragment = new EditProfile();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void setdetails(String uname, String umob, String uemail, String uadd) {
        username.setText(uname);
        uaddress.setText(uadd);
        umobnum.setText(umob);
        uemailadd.setText(uemail);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        username = v.findViewById(R.id.u_name);
        uaddress = v.findViewById(R.id.u_address);
        umobnum = v.findViewById(R.id.u_mob_number);
        uemailadd = v.findViewById(R.id.u_email);
        save = v.findViewById(R.id.save_profile);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userpref", Context.MODE_PRIVATE);
        uname = sharedPreferences.getString("customer_name","NA");
        umob = sharedPreferences.getString("customer_phone","NA");
        uemail = sharedPreferences.getString("customer_email","NA");
        uadd = sharedPreferences.getString("address","");
        cus_id = sharedPreferences.getString("customer_id","");

        setdetails(uname,umob,uemail,uadd);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatedname = username.getText().toString();
                updatedadd = uaddress.getText().toString();

                if(updatedname.equals("") || updatedname == null || updatedadd.equals("") || updatedadd == null)
                {
                    Toast.makeText(getContext(),"Name or Address cannot be empty",Toast.LENGTH_SHORT).show();
                }
                else {
                    update(cus_id,updatedname,updatedadd);
                }

            }
        });
        return v;
    }

    private void update(String cus_id, String updatedname, String updatedadd) {
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/editProfile");
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
                                JSONObject json2 = json.getJSONObject("getProduct");
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                if(stat.equals("true"))
                                {
                                    String msg = json2.getString("Message");
                                    JSONObject data = json2.getJSONObject("data");
                                    String cusname = data.getString("customer_name");
                                    String cusadd = data.getString("address");

                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userpref", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("customer_name",cusname);
                                    editor.putString("address",cusadd);
                                    editor.apply();

                                    Toast.makeText(getContext(),""+msg,Toast.LENGTH_SHORT).show();

                                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
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
                params.put("customer_name",updatedname);
                params.put("address",updatedadd);
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