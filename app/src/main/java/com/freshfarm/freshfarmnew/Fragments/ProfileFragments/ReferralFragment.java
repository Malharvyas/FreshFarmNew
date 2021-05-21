package com.freshfarm.freshfarmnew.Fragments.ProfileFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.freshfarm.freshfarmnew.R;


public class ReferralFragment extends Fragment implements View.OnClickListener {

    String url = "",cus_id = "",ref_code = "";
    TextView tvref;
    CardView share_ref;

    public ReferralFragment() {
        // Required empty public constructor
    }

    public static ReferralFragment newInstance(String param1, String param2) {
        ReferralFragment fragment = new ReferralFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userpref", Context.MODE_PRIVATE);
        ref_code = sharedPreferences.getString("ref_code", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_referral, container, false);

        tvref = v.findViewById(R.id.tvref_code);
        share_ref = v.findViewById(R.id.share_ref);

        if(ref_code != null)
        {
            tvref.setText(ref_code);
        }

        share_ref.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.share_ref:
            {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "FreshFarm");
                    String shareMessage= "\nHey I am sharing you an amazing application, please login with my referral code ("+ref_code+") to get your welcome gift\n\n";
                    shareMessage = shareMessage + "Application Link will be added here";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Choose Application"));
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            break;
        }
    }
}