package com.example.freshfarmnew.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freshfarmnew.Interfaces.CartCallBack;
import com.example.freshfarmnew.Interfaces.WishListCallBack;
import com.example.freshfarmnew.Model.CartModel;
import com.example.freshfarmnew.Model.ProductVariation;
import com.example.freshfarmnew.Model.WishListModel;
import com.example.freshfarmnew.Model.WishListVariation;
import com.example.freshfarmnew.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {

    private Context context;
    private List<WishListModel> list;
    private WishListCallBack wishListCallBack;

    public WishListAdapter(Context context, List<WishListModel> list, WishListCallBack wishListCallBack) {
        this.context = context;
        this.wishListCallBack = wishListCallBack;
        this.list = list;
    }

    @NonNull
    @Override
    public WishListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_whishlist, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WishListAdapter.ViewHolder holder, int position) {
        WishListModel wishListModel = list.get(position);

        List<WishListVariation> wishListVariationList = wishListModel.getVariation();
        ArrayList<String> stringArrayList = new ArrayList<>();
        if (wishListVariationList != null) {
            for (int i = 0; i < wishListVariationList.size(); i++) {
                WishListVariation variation = wishListVariationList.get(i);
                String unit_val = variation.getUnitVal();
                String unit = variation.getUnit();
                String combine = unit_val + " " + unit;
                stringArrayList.add(combine);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner_text, stringArrayList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinnerVer.setAdapter(adapter);
        }

        holder.tvName.setText(wishListModel.getProductName());

        Picasso.get().load(wishListModel.getProductImage()).fit().into(holder.imageView);

        holder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wishListCallBack.updateWishList(position, wishListModel.getProductId(), wishListModel.getCustomerId());
            }
        });

        holder.spinnerVer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (int j = 0; j < stringArrayList.size(); j++) {
                    WishListVariation wlVeriation = wishListVariationList.get(j);
                    String item = wlVeriation.getUnitVal() + " " + wlVeriation.getUnit();
                    String selected = holder.spinnerVer.getSelectedItem().toString();
                    if (item.equals(selected)) {
                        holder.tvPrice.setText("\u20B9 " + wlVeriation.getPrice());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivRemove;
        ImageView imageView;
        TextView tvName, tvPrice;
        Spinner spinnerVer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivRemove = itemView.findViewById(R.id.ivRemove);
            imageView = itemView.findViewById(R.id.imageView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            spinnerVer = itemView.findViewById(R.id.spinnerVer);
        }
    }
}

