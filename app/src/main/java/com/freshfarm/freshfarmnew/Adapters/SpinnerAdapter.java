package com.freshfarm.freshfarmnew.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.freshfarm.freshfarmnew.Model.ProductVariation;
import com.freshfarm.freshfarmnew.R;

import java.util.List;

public class SpinnerAdapter extends BaseAdapter {
    private final List<ProductVariation> list;
    Context context;
    LayoutInflater inflter;

    public SpinnerAdapter(Context applicationContext, List<ProductVariation> list) {
        this.context = applicationContext;
        this.list = list;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.spinner_text, null);
        TextView tvSpinner = (TextView) view.findViewById(R.id.tvSpinner);
        tvSpinner.setText(list.get(i).unit_val + " " + list.get(i).unit);
        return view;
    }
}
