package com.example.freshfarmnew.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.freshfarmnew.Fragments.ProfileFragments.ChangePassword;
import com.example.freshfarmnew.Fragments.ProfileFragments.EditProfile;
import com.example.freshfarmnew.MainActivity;
import com.example.freshfarmnew.R;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    EditText edit_profile, change_pass, referral_code, logout;

    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile2, container, false);
        edit_profile = v.findViewById(R.id.edit_profile);
        change_pass = v.findViewById(R.id.change_pass);
        referral_code = v.findViewById(R.id.referral_code);
        logout = v.findViewById(R.id.nav_logout2);

        edit_profile.setOnClickListener(this);
        change_pass.setOnClickListener(this);
        referral_code.setOnClickListener(this);
        logout.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_profile: {
                EditProfile p = new EditProfile();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("edit_profile");
                ft.replace(R.id.fragment_container, p);
                ft.commit();
            }
            break;
            case R.id.change_pass: {
                ChangePassword p = new ChangePassword();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("change_pass");
                ft.replace(R.id.fragment_container, p);
                ft.commit();
            }
            break;
            case R.id.referral_code: {

            }
            break;
            case R.id.nav_logout2: {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userlogin", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("islogin", false);
                editor.apply();
                SharedPreferences sharedPreferences1 = getActivity().getSharedPreferences("userpref",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                editor1.clear();
                editor1.apply();
                SharedPreferences sharedPreferences2 = getActivity().getSharedPreferences("cartdetails",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                editor2.clear();
                editor2.apply();
                SharedPreferences sharedPreferences3 = getActivity().getSharedPreferences("temp",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor3 = sharedPreferences3.edit();
                editor3.clear();
                editor3.apply();
                startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().finish();
            }
            break;
        }
    }
}