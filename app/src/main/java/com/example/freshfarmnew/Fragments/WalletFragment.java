package com.example.freshfarmnew.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.freshfarmnew.R;


public class WalletFragment extends Fragment implements View.OnClickListener {

    TextView t1000,t2000,t3000,t4000;
    EditText amount;

    public WalletFragment() {
        // Required empty public constructor
    }

    public static WalletFragment newInstance(String param1, String param2) {
        WalletFragment fragment = new WalletFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wallet, container, false);
        t1000 = v.findViewById(R.id.t1000);
        t2000 = v.findViewById(R.id.t2000);
        t3000 = v.findViewById(R.id.t3000);
        t4000 = v.findViewById(R.id.t4000);
        amount = v.findViewById(R.id.amount);

        t1000.setOnClickListener(this);
        t2000.setOnClickListener(this);
        t3000.setOnClickListener(this);
        t4000.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.t1000:
            {
                amount.setText("1000");
            }
            break;
            case R.id.t2000:
            {
                amount.setText("2000");
            }
            break;
            case R.id.t3000:
            {
                amount.setText("3000");
            }
            break;
            case R.id.t4000:
            {
                amount.setText("4000");
            }
            break;
        }

    }
}