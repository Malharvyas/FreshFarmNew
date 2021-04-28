package com.example.freshfarmnew.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.freshfarmnew.Fragments.ProfileFragments.ChangePassword;
import com.example.freshfarmnew.Fragments.ProfileFragments.EditProfile;
import com.example.freshfarmnew.R;



public class ProfileFragment extends Fragment implements View.OnClickListener {

    EditText edit_profile,change_pass,referral_code,logout;

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
        switch (v.getId())
        {
            case R.id.edit_profile:
            {
                EditProfile p = new EditProfile();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("edit_profile");
                ft.replace(R.id.fragment_container,p);
                ft.commit();
            }
            break;
            case R.id.change_pass:
            {
                ChangePassword p = new ChangePassword();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("change_pass");
                ft.replace(R.id.fragment_container,p);
                ft.commit();
            }
            break;
            case R.id.referral_code:
            {

            }
            break;
            case  R.id.nav_logout2:
            {

            }
            break;
        }
    }
}