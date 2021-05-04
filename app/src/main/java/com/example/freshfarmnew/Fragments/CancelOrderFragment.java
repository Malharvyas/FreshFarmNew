package com.example.freshfarmnew.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.example.freshfarmnew.Adapters.CheckOutAdapter;
import com.example.freshfarmnew.Class.BaseUrl;
import com.example.freshfarmnew.Interfaces.AddressCallBack;
import com.example.freshfarmnew.Model.AddressDataModel;
import com.example.freshfarmnew.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CancelOrderFragment extends Fragment {

    String url = "", cus_id = "";
    private ProgressBar progressbar;
    private TextView tvOrderID;
    private Spinner spinnerCancel;
    private EditText edComment;
    private Button btnCancelOrder;

    public CancelOrderFragment() {
        // Required empty public constructor
    }

    public static CancelOrderFragment newInstance(String param1, String param2) {
        CancelOrderFragment fragment = new CancelOrderFragment();
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
        View v = inflater.inflate(R.layout.fragment_cancel_order, container, false);

        progressbar = v.findViewById(R.id.progressbar);
        tvOrderID = v.findViewById(R.id.tvOrderID);
        spinnerCancel = v.findViewById(R.id.spinnerCancel);
        edComment = v.findViewById(R.id.edComment);
        btnCancelOrder = v.findViewById(R.id.btnCancelOrder);

        if (getArguments() != null) {
            if (getArguments().containsKey("OrderId")) {
                tvOrderID.setText(getArguments().getString("OrderId"));
            }
        }

        btnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getCancelOrder();
            }
        });

        return v;
    }

    private void getCancelOrder() {
        
    }
}