package com.freshfarm.freshfarmnew.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.freshfarm.freshfarmnew.Interfaces.AddressCallBack;
import com.freshfarm.freshfarmnew.Model.AddressDataModel;
import com.freshfarm.freshfarmnew.R;

import java.util.List;

public class CheckOutAdapter extends RecyclerView.Adapter<CheckOutAdapter.ViewHolder> {

    private Context context;
    private List<AddressDataModel> list;
    private AddressCallBack addressCallBack;
    public int mSelectedItem = -1;

    public CheckOutAdapter(Context context, List<AddressDataModel> list, AddressCallBack addressCallBack) {
        this.context = context;
        this.addressCallBack = addressCallBack;
        this.list = list;
    }

    @NonNull
    @Override
    public CheckOutAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_checkout_address, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckOutAdapter.ViewHolder holder, int position) {
        if (position == mSelectedItem) {
            holder.radioBtnItem.setChecked(true);
            mSelectedItem = -1;
        } else {
            holder.radioBtnItem.setChecked(false);
        }

        AddressDataModel addressDataModel = list.get(position);

        holder.tvAddress.setText(addressDataModel.getAddress());
        holder.tvName.setText(addressDataModel.getContactName());
        holder.tvPhone.setText(addressDataModel.getPhoneNumber());

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressCallBack.removeAddress(position, addressDataModel.getAddressId());
            }
        });

        holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressCallBack.updateAddress(addressDataModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RadioButton radioBtnItem;
        Button btnDelete, btnUpdate;
        TextView tvName, tvAddress, tvPhone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            radioBtnItem = itemView.findViewById(R.id.radioBtnItem);
            tvName = itemView.findViewById(R.id.tvName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvPhone = itemView.findViewById(R.id.tvPhone);

            radioBtnItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    addressCallBack.onAddressSelect(list.get(getAdapterPosition()).getAddressId());

                    mSelectedItem = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
        }
    }
}

