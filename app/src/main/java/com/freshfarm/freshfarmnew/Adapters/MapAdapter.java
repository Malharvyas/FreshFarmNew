package com.freshfarm.freshfarmnew.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.freshfarm.freshfarmnew.Model.AreaModel;
import com.freshfarm.freshfarmnew.Model.Category;
import com.freshfarm.freshfarmnew.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MapAdapter extends RecyclerView.Adapter<MapAdapter.ViewHolder> {

    private Context context;
    private List<AreaModel> list;
    private onClickListener monclicklistener;

    public MapAdapter(Context context, List<AreaModel> list, onClickListener monclicklistener) {
        this.context = context;
        this.list = list;
        this.monclicklistener = monclicklistener;
    }

    @NonNull
    @Override
    public MapAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        v = LayoutInflater.from(context).inflate(R.layout.item_map, parent, false);
        return new ViewHolder(v, monclicklistener);
    }

    @Override
    public void onBindViewHolder(@NonNull MapAdapter.ViewHolder holder, int position) {
        AreaModel areaModel = list.get(position);
        holder.areaName.setText(areaModel.getAreaName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        onClickListener onClickListener;
        TextView areaName;

        public ViewHolder(@NonNull View itemView, onClickListener onClickListener) {
            super(itemView);

            this.onClickListener = onClickListener;
            areaName = itemView.findViewById(R.id.areaName);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickListener.onClicked(getAdapterPosition());
        }
    }

    public interface onClickListener {
        void onClicked(int position);
    }
}
