package com.example.freshfarmnew.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freshfarmnew.Interfaces.WishListCallBack;
import com.example.freshfarmnew.Model.WishListModel;
import com.example.freshfarmnew.Model.WishListVariation;
import com.example.freshfarmnew.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder> {

    private Context context;
    private List<WishListModel> list;
    private WishListCallBack wishListCallBack;

    public MyOrdersAdapter(Context context, List<WishListModel> list, WishListCallBack wishListCallBack) {
        this.context = context;
        this.wishListCallBack = wishListCallBack;
        this.list = list;
    }

    @NonNull
    @Override
    public MyOrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_whishlist, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrdersAdapter.ViewHolder holder, int position) {
        WishListModel wishListModel = list.get(position);

        //holder.tvName.setText(wishListModel);
        //holder.tvPrice.setText("Price :" + wishListModel.);
        /*if (cartModel.getPrice() != null &&   .getQuantity() != null) {
            double finalPrice = Double.parseDouble(cartModel.getPrice()) * Double.parseDouble(cartModel.getQuantity());
            //holder.tvFinalPrice.setText(String.valueOf(finalPrice));
        }*/

        Picasso.get().load(wishListModel.getProductImage()).fit().into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView tvName, tvPrice, tvProductName, tvQuntity, tvTotalPrice, tvContactDetails;
        Button btnCustomerInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuntity = itemView.findViewById(R.id.tvQuntity);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            btnCustomerInfo = itemView.findViewById(R.id.btnCustomerInfo);
            tvName = itemView.findViewById(R.id.tvName);
            tvContactDetails = itemView.findViewById(R.id.tvContactDetails);
        }
    }
}

