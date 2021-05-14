package com.example.freshfarmnew.Adapters;



import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freshfarmnew.Model.Product;
import com.example.freshfarmnew.Model.ProductVariation;
import com.example.freshfarmnew.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchProductAdapter extends RecyclerView.Adapter<SearchProductAdapter.ViewHolder>{

    private Context context;
    private List<Product> list;
    private onClickListener monclicklistener;

    public SearchProductAdapter(Context context, List<Product> list, onClickListener monclicklistener) {
        this.context = context;
        this.list = list;
        this.monclicklistener = monclicklistener;
    }

    @NonNull
    @Override
    public SearchProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.product_layout2,parent,false);
        return new ViewHolder(v,monclicklistener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchProductAdapter.ViewHolder holder, int position) {
        Product pro = list.get(position);

        if(pro.getLiked() == true){
            holder. sav_pro.setImageResource(R.drawable.heart_asset_filled);
            holder.sav_pro.setTag("filled");
        }

        holder.pro_name.setText(pro.getProduct_name());
//        holder.pro_price.setText("\u20B9 " + pro.getProduct_price());

        List<ProductVariation> pvlist = pro.getVariations();
        ArrayList<String> stringArrayList = new ArrayList<String>();
        if(pvlist != null)
        {
            for (int i = 0; i < pvlist.size(); i++)
            {
                ProductVariation variation = pvlist.get(i);
                String unit_val = variation.getUnit_val();
                String unit = variation.getUnit();
                String combine = unit_val + " " + unit;
                stringArrayList.add(combine);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_text,stringArrayList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.varspineer.setAdapter(adapter);
        }

        Picasso.get().load(pro.getProduct_image()).fit().into(holder.pro_img);

        holder.varspineer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for(int j = 0; j < stringArrayList.size(); j++)
                {
                    ProductVariation pv = pvlist.get(j);
                    String item = pv.getUnit_val()+" "+pv.getUnit();
                    String selected = holder.varspineer.getSelectedItem().toString();
                    if(item.equals(selected))
                    {
                        holder.pro_price.setText("\u20B9 " + pv.getPrice());
                        SharedPreferences sharedPreferences = context.getSharedPreferences("temp",Context.MODE_PRIVATE);
                        int proidcheck = sharedPreferences.getInt(pv.getProduct_id(),0);
                        if(proidcheck == 1)
                        {
//                            Toast.makeText(context,"check1",Toast.LENGTH_SHORT).show();
                            String key2 = pv.getProduct_id()+pv.getV_id();
                            int vidcheck = sharedPreferences.getInt(key2,0);
                            if(vidcheck == 1)
                            {
                                holder.plus.setVisibility(View.VISIBLE);
                                holder.minus.setVisibility(View.VISIBLE);
                                holder.quant_val.setVisibility(View.VISIBLE);
                                holder.trend_add.setVisibility(View.GONE);

                                String key = key2+"quant";

                                int quantity = sharedPreferences.getInt(key,1);
                                holder.quant_val.setText(""+quantity);
//                                Toast.makeText(context,"check2",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                holder.plus.setVisibility(View.GONE);
                                holder.minus.setVisibility(View.GONE);
                                holder.quant_val.setVisibility(View.GONE);
                                holder.trend_add.setVisibility(View.VISIBLE);
                            }
                        }
                        else{
                            holder.plus.setVisibility(View.GONE);
                            holder.minus.setVisibility(View.GONE);
                            holder.quant_val.setVisibility(View.GONE);
                            holder.trend_add.setVisibility(View.VISIBLE);
                        }
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        onClickListener onClickListener;
        ImageView pro_img,sav_pro;
        TextView pro_name,pro_price;
        Button trend_add;
        Spinner varspineer;
        TextView plus,minus,quant_val;

        public ViewHolder(@NonNull View itemView, onClickListener onClickListener) {
            super(itemView);

            this.onClickListener = onClickListener;
            pro_img = itemView.findViewById(R.id.product_img);
            pro_name = itemView.findViewById(R.id.product_name);
            pro_price = itemView.findViewById(R.id.product_price);
            varspineer = itemView.findViewById(R.id.variant_spinner);
            trend_add = itemView.findViewById(R.id.add_product);
            plus = itemView.findViewById(R.id.plus);
            minus = itemView.findViewById(R.id.minus);
            quant_val = itemView.findViewById(R.id.quant_val);
            sav_pro = itemView.findViewById(R.id.save_product);

            pro_img.setOnClickListener(this);

            trend_add.setOnClickListener(this);

            plus.setOnClickListener(this);

            minus.setOnClickListener(this);

            sav_pro.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.product_img:
                {
                    onClickListener.onProductClicked(getAdapterPosition());
                }
                break;
                case R.id.add_product:
                {
                    SharedPreferences sharedPreferences2 = context.getSharedPreferences("userlogin", Context.MODE_PRIVATE);
                    Boolean isLogin = sharedPreferences2.getBoolean("islogin", false);

                    if(isLogin ==  false)
                    {
                        Toast.makeText(context,"Please login to continue",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        trend_add.setVisibility(View.INVISIBLE);
                        minus.setVisibility(View.VISIBLE);
                        plus.setVisibility(View.VISIBLE);
                        quant_val.setVisibility(View.VISIBLE);
                        Product pro = list.get(getAdapterPosition());
                        List<ProductVariation> pvlist = pro.getVariations();
                        ArrayList<String> stringArrayList = new ArrayList<String>();
                        if(pvlist != null)
                        {
                            for (int i = 0; i < pvlist.size(); i++)
                            {
                                ProductVariation variation = pvlist.get(i);
                                String unit_val = variation.getUnit_val();
                                String unit = variation.getUnit();
                                String combine = unit_val + " " + unit;
                                String selected = varspineer.getSelectedItem().toString();
                                if(combine.equals(selected))
                                {
                                    SharedPreferences sharedPreferences = context.getSharedPreferences("temp",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putInt(variation.getProduct_id(),1);
                                    String key2 = variation.getProduct_id()+variation.getV_id();
                                    editor.putInt(key2,1);
                                    String key = key2+"quant";
                                    editor.putInt(key,1);
                                    editor.apply();
                                    quant_val.setText("1");
                                    onClickListener.onPAddClicked(getAdapterPosition(),"1",variation.getProduct_id(),variation.getV_id());
                                }
                            }
                        }
                    }

                }
                break;
                case R.id.plus:
                {
                    int quant = Integer.parseInt(quant_val.getText().toString());
                    quant +=1;
                    quant_val.setText(""+quant);
                    Product pro = list.get(getAdapterPosition());
                    List<ProductVariation> pvlist = pro.getVariations();
                    if(pvlist != null)
                    {
                        for (int i = 0; i < pvlist.size(); i++)
                        {
                            ProductVariation variation = pvlist.get(i);
                            String unit_val = variation.getUnit_val();
                            String unit = variation.getUnit();
                            String combine = unit_val + " " + unit;
                            String selected = varspineer.getSelectedItem().toString();
                            if(combine.equals(selected))
                            {
                                SharedPreferences sharedPreferences = context.getSharedPreferences("temp",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                String key2 = variation.getProduct_id()+variation.getV_id();
                                String key = key2+"quant";
                                int quanty = sharedPreferences.getInt(key,1);
                                quanty += 1;
                                editor.putInt(key,quanty);
                                editor.apply();
                                onClickListener.onPAddClicked(getAdapterPosition(),""+quanty,variation.getProduct_id(),variation.getV_id());
                            }
                        }
                    }
                }
                break;
                case R.id.minus:
                {
                    int quant = Integer.parseInt(quant_val.getText().toString());
                    if(quant > 1)
                    {
                        quant -= 1;
                        quant_val.setText(""+quant);
                        Product pro = list.get(getAdapterPosition());
                        List<ProductVariation> pvlist = pro.getVariations();
                        ArrayList<String> stringArrayList = new ArrayList<String>();
                        if(pvlist != null)
                        {
                            for (int i = 0; i < pvlist.size(); i++)
                            {
                                ProductVariation variation = pvlist.get(i);
                                String unit_val = variation.getUnit_val();
                                String unit = variation.getUnit();
                                String combine = unit_val + " " + unit;
                                String selected = varspineer.getSelectedItem().toString();
                                if(combine.equals(selected))
                                {
                                    SharedPreferences sharedPreferences = context.getSharedPreferences("temp",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putInt(variation.getProduct_id(),1);
                                    String key2 = variation.getProduct_id()+variation.getV_id();
                                    editor.putInt(key2,1);
                                    String key = key2+"quant";
                                    int quanty = sharedPreferences.getInt(key,1);
                                    quanty -= 1;
                                    editor.putInt(key,quanty);
                                    editor.apply();
                                    onClickListener.onPAddClicked(getAdapterPosition(),""+quanty,variation.getProduct_id(),variation.getV_id());
                                }
                            }
                        }
                    }
                    else{
                        minus.setVisibility(View.GONE);
                        plus.setVisibility(View.GONE);
                        quant_val.setVisibility(View.GONE);
                        trend_add.setVisibility(View.VISIBLE);
                        Product pro = list.get(getAdapterPosition());
                        List<ProductVariation> pvlist = pro.getVariations();
                        if(pvlist != null)
                        {
                            for (int i = 0; i < pvlist.size(); i++)
                            {
                                ProductVariation variation = pvlist.get(i);
                                String unit_val = variation.getUnit_val();
                                String unit = variation.getUnit();
                                String combine = unit_val + " " + unit;
                                String selected = varspineer.getSelectedItem().toString();
                                if(combine.equals(selected))
                                {
                                    SharedPreferences sharedPreferences = context.getSharedPreferences("temp",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putInt(variation.getProduct_id(),1);
                                    String key2 = variation.getProduct_id()+variation.getV_id();
                                    editor.putInt(key2,0);
                                    String key = key2+"quant";
                                    editor.putInt(key,0);
                                    editor.apply();
                                    onClickListener.onPAddClicked(getAdapterPosition(),"0",variation.getProduct_id(),variation.getV_id());
                                }
                            }
                        }
                    }
                }
                break;
                case R.id.save_product:
                {
                    Product pro = list.get(getAdapterPosition());
                    if(((String)sav_pro.getTag()).equals("stroked"))
                    {
                        sav_pro.setImageResource(R.drawable.heart_asset_filled);
                        sav_pro.setTag("filled");
                        onClickListener.onSaved(getAdapterPosition(),pro.getProduct_id(),1);
                    }
                    else if(((String)sav_pro.getTag()).equals("filled"))
                    {
                        sav_pro.setImageResource(R.drawable.heart_asset);
                        sav_pro.setTag("stroked");
                        onClickListener.onSaved(getAdapterPosition(),pro.getProduct_id(),0);
                    }

                }
            }

        }
    }

    public interface onClickListener{
        void onProductClicked(int position);
        void onPAddClicked(int position,String quantity,String product_id,String v_id);
        void onSaved(int position,String product_id,int quantity);
    }
}
