package com.example.freshfarmnew.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freshfarmnew.Model.Category;
import com.example.freshfarmnew.Model.SubCategory;
import com.example.freshfarmnew.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ViewHolder> {

    private Context context;
    private List<SubCategory> list;
    private onClickListener monclicklistener;
    private String frag_id;

    public SubCategoryAdapter(Context context, List<SubCategory> list, onClickListener monclicklistener, String frag_id) {
        this.context = context;
        this.list = list;
        this.monclicklistener = monclicklistener;
        this.frag_id = frag_id;
    }

    @NonNull
    @Override
    public SubCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cateogory_layout,parent,false);
        return new ViewHolder(v,monclicklistener);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoryAdapter.ViewHolder holder, int position) {
        SubCategory cat =list.get(position);
        holder.cat_name.setText(cat.getSubcategory_name());

        Picasso.get().load(cat.getImage()).fit().into(holder.cat_img);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        onClickListener onClickListener;
        TextView cat_name;
        ImageView cat_img;

        public ViewHolder(@NonNull View itemView,onClickListener onClickListener) {
            super(itemView);

            this.onClickListener = onClickListener;
            cat_name = itemView.findViewById(R.id.cat_name);
            cat_img = itemView.findViewById(R.id.cat_imgg);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickListener.onClicked(getAdapterPosition());
        }
    }

    public interface onClickListener{
        void onClicked(int position);
    }
}
