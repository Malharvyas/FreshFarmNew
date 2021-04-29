package com.example.freshfarmnew.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.freshfarmnew.R;


public class WishlistFragment extends Fragment {

    String url = "", cus_id = "";
    private ConstraintLayout emptyWhishList;
    private LinearLayout nonemptyWhishList;
    private RecyclerView recyclerView;
    private ProgressBar progressbar;
    private Button btnExplore;

    public WishlistFragment() {
        // Required empty public constructor
    }

    public static WishlistFragment newInstance(String param1, String param2) {
        WishlistFragment fragment = new WishlistFragment();
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
        View v = inflater.inflate(R.layout.fragment_wishlist, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        progressbar = v.findViewById(R.id.progressbar);
        emptyWhishList = v.findViewById(R.id.emptyWhishList);
        nonemptyWhishList = v.findViewById(R.id.nonemptyWhishList);
        btnExplore = v.findViewById(R.id.btnExplore);

        return v;
    }
}