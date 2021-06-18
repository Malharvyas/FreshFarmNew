package com.freshfarm.freshfarmnew.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.freshfarm.freshfarmnew.Interfaces.CancelOrderCallBack;
import com.freshfarm.freshfarmnew.Model.OrderListModel;
import com.freshfarm.freshfarmnew.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder> {

    private Context context;
    private List<OrderListModel> list;
    private CancelOrderCallBack cancelOrderCallBack;

    public MyOrdersAdapter(Context context, List<OrderListModel> list, CancelOrderCallBack cancelOrderCallBack) {
        this.context = context;
        this.cancelOrderCallBack = cancelOrderCallBack;
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
        holder.tvOrderDate.setText(parseDateToddMMyyyy(model.getOrderTime()));

        /*if (model.getStatus().equals("cancel")) {
            holder.btnCancelOrder.setVisibility(View.GONE);
        } else {
            holder.btnCancelOrder.setVisibility(View.VISIBLE);
        }*/

        holder.btnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrderCallBack.cancelOrder(model.getOrderId());
            }
        });

        holder.btnOrderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrderCallBack.orderDetails(model.getOrderId());
            }
        });
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
//            btnOrderTrack = itemView.findViewById(R.id.btnOrderTrack);
        }
    }

    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd MMM yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
}

