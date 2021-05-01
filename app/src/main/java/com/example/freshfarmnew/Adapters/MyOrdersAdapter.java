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
import com.example.freshfarmnew.Model.OrderListModel;
import com.example.freshfarmnew.Model.WishListModel;
import com.example.freshfarmnew.Model.WishListVariation;
import com.example.freshfarmnew.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder> {

    private Context context;
    private List<OrderListModel> list;
    private WishListCallBack wishListCallBack;

    public MyOrdersAdapter(Context context, List<OrderListModel> list, WishListCallBack wishListCallBack) {
        this.context = context;
        this.wishListCallBack = wishListCallBack;
        this.list = list;
    }

    @NonNull
    @Override
    public MyOrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_my_orders, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrdersAdapter.ViewHolder holder, int position) {
        OrderListModel model = list.get(position);

        holder.tvOrderID.setText(model.getOrderId());
        holder.tvItem.setText(model.getProducts().size() + " Item");
        holder.tvStatus.setText(model.getStatus());
        holder.tvTotalPrice.setText("\u20B9" + " " + model.getTotalAmount());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvOrderID, tvItem, tvOrderDate, tvStatus, tvTotalPrice;
        Button btnCancelOrder, btnOrderDetails, btnOrderTrack;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvOrderID = itemView.findViewById(R.id.tvOrderID);
            tvItem = itemView.findViewById(R.id.tvItem);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            btnCancelOrder = itemView.findViewById(R.id.btnCancelOrder);
            btnOrderDetails = itemView.findViewById(R.id.btnOrderDetails);
            btnOrderTrack = itemView.findViewById(R.id.btnOrderTrack);
        }
    }
}

