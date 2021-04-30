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
import com.example.freshfarmnew.Model.Category;
import com.example.freshfarmnew.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context context;
    private List<CartModel> list;
    private CartCallBack cartCallBack;

    public CartAdapter(Context context, List<CartModel> list, CartCallBack cartCallBack) {
        this.context = context;
        this.cartCallBack = cartCallBack;
        this.list = list;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        CartModel cartModel = list.get(position);
        holder.tvName.setText(cartModel.getProductName()+" ( "+cartModel.getUnitVal()+" "+cartModel.getUnit()+" )");
        holder.tvPrice.setText("Price :" + cartModel.getPrice());
        if (cartModel.getPrice() != null && cartModel.getQuantity() != null) {
            double finalPrice = Double.parseDouble(cartModel.getPrice()) * Double.parseDouble(cartModel.getQuantity());
            holder.tvFinalPrice.setText(String.valueOf(finalPrice));
        }
        holder.tvQty.setText(cartModel.getQuantity());

        Picasso.get().load(cartModel.getProductImage()).fit().into(holder.imageView);

        holder.tvPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(cartModel.getQuantity());
                qty++;
                cartModel.setQuantity(String.valueOf(qty));
                holder.tvQty.setText(cartModel.getQuantity());
                cartCallBack.updateCart(cartModel.getProductId(), cartModel.getvId(), cartModel.getQuantity());
            }
        });

        holder.tvMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(cartModel.getQuantity());
                if (qty > 1) {
                    qty--;
                    cartModel.setQuantity(String.valueOf(qty));
                    holder.tvQty.setText(cartModel.getQuantity());
                    cartCallBack.updateCart(cartModel.getProductId(), cartModel.getvId(), cartModel.getQuantity());
                }
            }
        });

        holder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*SharedPreferences sharedPreferences = context.getSharedPreferences("temp", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(cartModel.getProductId(), 1);
                String key2 = cartModel.getProductId() + cartModel.getvId();
                editor.putInt(key2, 0);
                String key = key2 + "quant";
                editor.putInt(key, 0);
                editor.apply();*/
                cartCallBack.updateCart(cartModel.getProductId(), cartModel.getvId(), "0");
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
        TextView tvName, tvPrice, tvFinalPrice, tvPlus, tvQty, tvMinus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivRemove = itemView.findViewById(R.id.ivRemove);
            imageView = itemView.findViewById(R.id.imageView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvFinalPrice = itemView.findViewById(R.id.tvFinalPrice);
            tvPlus = itemView.findViewById(R.id.tvPlus);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvMinus = itemView.findViewById(R.id.tvMinus);

        }
    }
}

