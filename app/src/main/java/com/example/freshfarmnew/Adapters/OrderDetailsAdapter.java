package com.example.freshfarmnew.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freshfarmnew.Interfaces.CartCallBack;
import com.example.freshfarmnew.Model.CartModel;
import com.example.freshfarmnew.Model.OrderDetailsModel;
import com.example.freshfarmnew.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder> {

    private Context context;
    private List<OrderDetailsModel> list;
    private CartCallBack cartCallBack;

    public OrderDetailsAdapter(Context context, List<OrderDetailsModel> list, CartCallBack cartCallBack) {
        this.context = context;
        this.cartCallBack = cartCallBack;
        this.list = list;
    }

    @NonNull
    @Override
    public OrderDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_order_details, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailsAdapter.ViewHolder holder, int position) {
        OrderDetailsModel model = list.get(position);

        //holder.tvName.setText(cartModel.getProductName() + " ( " + cartModel.getUnitVal() + " " + cartModel.getUnit() + " )");
        //holder.tvPrice.setText("Price :" + cartModel.getPrice());
       /* if (cartModel.getPrice() != null && cartModel.getQuantity() != null) {
            double finalPrice = Double.parseDouble(cartModel.getPrice()) * Double.parseDouble(cartModel.getQuantity());
            holder.tvFinalPrice.setText("\u20B9" + " " + String.valueOf(finalPrice));
        }*/
        //holder.tvQty.setText(cartModel.getQuantity());

        //Picasso.get().load(cartModel.getProductImage()).fit().into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView tvName, tvPrice, tvFinalPrice, tvQty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            tvName = itemView.findViewById(R.id.tvName);
            tvFinalPrice = itemView.findViewById(R.id.tvFinalPrice);
            tvQty = itemView.findViewById(R.id.tvQty);

        }
    }
}

