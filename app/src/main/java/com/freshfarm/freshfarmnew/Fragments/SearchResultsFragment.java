package com.freshfarm.freshfarmnew.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.freshfarm.freshfarmnew.Adapters.SearchProductAdapter;
import com.freshfarm.freshfarmnew.Class.BaseUrl;
import com.freshfarm.freshfarmnew.Model.Product;
import com.freshfarm.freshfarmnew.Model.ProductVariation;
import com.freshfarm.freshfarmnew.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResultsFragment extends Fragment implements SearchProductAdapter.onClickListener{

    String url = "",key = "",cus_id="";
    RecyclerView search_recycler;
    RecyclerView.Adapter adapter;
    List<Product> prolist;
    List<ProductVariation> variantionlist;
    String category_id,sub_cat_id,all_pro_id="1";
    ProgressBar progressBar;
    int c1 = 0;

    public SearchResultsFragment() {
        // Required empty public constructor
    }

    public static SearchResultsFragment newInstance(String param1, String param2) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prolist = new ArrayList<>();
        adapter = new SearchProductAdapter(getContext(),prolist,this);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userpref", Context.MODE_PRIVATE);
        cus_id = sharedPreferences.getString("customer_id","");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_results, container, false);

        if(getArguments() != null)
        {
            key = getArguments().getString("search_key");
        }

        search_recycler = v.findViewById(R.id.search_recycler);
        progressBar = v.findViewById(R.id.progressbar);

        search_recycler.setHasFixedSize(true);
        search_recycler.setItemAnimator(new DefaultItemAnimator());
        search_recycler.setAdapter(adapter);

        progressBar.setVisibility(View.VISIBLE);

        getProducts(key);

        if(c1 == 1)
        {
            progressBar.setVisibility(View.GONE);
        }
        return v;
    }

    private void getProducts(String key) {
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/Search");
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Printlog..",response);
                        BaseUrl b = new BaseUrl();
                        url = b.url;
                        if(response != null) {
                            JSONObject json = null;

                            try {
                                json = new JSONObject(String.valueOf(response));
                                JSONObject json2 = json.getJSONObject("Search");
                                Boolean status = json2.getBoolean("Status");
                                String stat = status.toString();
                                if(stat.equals("true"))
                                {
                                    prolist.clear();
                                    JSONObject dataobj = json2.getJSONObject("data");
                                    JSONArray data = dataobj.getJSONArray("product");
                                    for(int i = 0; i < data.length(); i++ )
                                    {
                                        Product product = new Product();
                                        JSONObject catobj = data.getJSONObject(i);
                                        String pro_id = catobj.getString("product_id");
                                        String pro_name = catobj.getString("product_name");
                                        String pro_img = catobj.getString("product_image");
                                        Boolean liked = catobj.getBoolean("liked");
                                        variantionlist = new ArrayList<>();
                                        JSONArray variant = catobj.getJSONArray("variant");
                                        for(int j = 0; j < variant.length(); j++)
                                        {
                                            ProductVariation pv = new ProductVariation();
                                            JSONObject varobj = variant.getJSONObject(j);
                                            String v_id = varobj.getString("v_id");
                                            String unit = varobj.getString("unit");
                                            String price = varobj.getString("price");
                                            String product_id = varobj.getString("product_id");
                                            String unit_val = varobj.getString("unit_val");
                                            pv.setV_id(v_id);
                                            pv.setProduct_id(product_id);
                                            pv.setPrice(price);
                                            pv.setUnit(unit);
                                            pv.setUnit_val(unit_val);
//                                            Toast.makeText(getContext(),""+unit,Toast.LENGTH_SHORT).show();
                                            variantionlist.add(pv);
                                        }
                                        product.setProduct_id(pro_id);
                                        product.setProduct_name(pro_name);
                                        product.setProduct_image(pro_img);
                                        product.setVariations(variantionlist);
                                        product.setLiked(liked);
                                        prolist.add(product);
                                    }

                                }
                                else if(stat.equals("false"))
                                {
                                    String msg = json2.getString("Message");
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                                }
//                                Toast.makeText(getContext(),""+catlist.size(),Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            adapter.notifyDataSetChanged();
                            c1 = 1;
                            if(c1 == 1)
                            {
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                BaseUrl b = new BaseUrl();
                url = b.url;
                if(error instanceof ClientError)
                {
                    try{
                        String responsebody = new String(error.networkResponse.data,"utf-8");
                        JSONObject data = new JSONObject(responsebody);
                        Boolean status = data.getBoolean("status");
                        String stat = status.toString();
                        if(stat.equals("false"))
                        {
                            String msg = data.getString("Message");
                            Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(getContext(),"Error : "+error,Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("key",key);
                if(cus_id.equals(null) || cus_id.equals(""))
                {

                }
                else{
                    params.put("customer_id",cus_id);
                }
                return params;
            }
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "u222436058_fresh_farm:tG9r6C5Q$";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", auth);
//                headers.put("x-api-key","HRCETCRACKER@123");
//                headers.put("Content-Type", "application/form-data");
                return headers;
            }

        };
        volleyRequestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
    }

    @Override
    public void onProductClicked(int position) {
        Product p = prolist.get(position);
        String p_id = p.getProduct_id();
        Bundle b = new Bundle();
        b.putString("product_id",p_id);
        ProductDetailsFragment pdf = new ProductDetailsFragment();
        pdf.setArguments(b);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("product_details");
        ft.replace(R.id.fragment_container,pdf);
        ft.commit();
    }

    @Override
    public void onPAddClicked(int position, String quantity, String product_id, String v_id) {
        progressBar.setVisibility(View.VISIBLE);
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/addtocart");
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        BaseUrl b = new BaseUrl();
                        url = b.url;
                        if(response != null) {
                            JSONObject json = null;

                            try {
                                json = new JSONObject(String.valueOf(response));
                                JSONObject json2 = json.getJSONObject("addtocart");
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                if(stat.equals("true"))
                                {
                                    progressBar.setVisibility(View.GONE);
                                    if(quantity.equals("1")){
                                        String msg = json2.getString("Message");
                                        Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                                    }
                                    if(quantity.equals("0")){
                                        String msg = "Item Removed Successfully";
                                        Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                                    }

                                }
                                else if(stat.equals("false"))
                                {
                                    progressBar.setVisibility(View.GONE);
                                    String msg = json2.getString("Message");
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                                }
//                                Toast.makeText(getApplicationContext(),""+response,Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                BaseUrl b = new BaseUrl();
                url = b.url;
                progressBar.setVisibility(View.GONE);
                if(error instanceof ClientError)
                {
                    try{
                        String responsebody = new String(error.networkResponse.data,"utf-8");
                        JSONObject data = new JSONObject(responsebody);
                        Boolean status = data.getBoolean("status");
                        String stat = status.toString();
                        if(stat.equals("false"))
                        {
                            String msg = data.getString("Message");
                            Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(getContext(),"Error : "+error,Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("customer_id", cus_id);
                params.put("product_id", product_id);
                params.put("v_id", v_id);
                params.put("quantity", quantity);
                return params;
            }

            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "u222436058_fresh_farm:tG9r6C5Q$";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", auth);
//                headers.put("x-api-key","HRCETCRACKER@123");
//                headers.put("Content-Type", "application/form-data");
                return headers;
            }

        };
        volleyRequestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
    }

    @Override
    public void onSaved(int position, String product_id, int quantity) {
        progressBar.setVisibility(View.VISIBLE);
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/addtowishlist");
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        BaseUrl b = new BaseUrl();
                        url = b.url;
                        if(response != null) {
                            JSONObject json = null;

                            try {
                                json = new JSONObject(String.valueOf(response));
                                JSONObject json2 = json.getJSONObject("addtocart");
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                if(stat.equals("true"))
                                {
                                    progressBar.setVisibility(View.GONE);
                                    if(quantity == 1)
                                    {
                                        Toast.makeText(getContext(),"Product Saved",Toast.LENGTH_SHORT).show();
                                    }
                                    else if(quantity == 0){
                                        Toast.makeText(getContext(),"Product UnSaved",Toast.LENGTH_SHORT).show();
                                    }

                                }
                                else if(stat.equals("false"))
                                {
                                    progressBar.setVisibility(View.GONE);
                                    String msg = json2.getString("Message");
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                                }
//                                Toast.makeText(getApplicationContext(),""+response,Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                BaseUrl b = new BaseUrl();
                url = b.url;
                progressBar.setVisibility(View.GONE);
                if(error instanceof ClientError)
                {
                    try{
                        String responsebody = new String(error.networkResponse.data,"utf-8");
                        JSONObject data = new JSONObject(responsebody);
                        Boolean status = data.getBoolean("status");
                        String stat = status.toString();
                        if(stat.equals("false"))
                        {
                            String msg = data.getString("Message");
                            Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(getContext(),"Error : "+error,Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("customer_id", cus_id);
                params.put("product_id", product_id);
                return params;
            }

            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "u222436058_fresh_farm:tG9r6C5Q$";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", auth);
//                headers.put("x-api-key","HRCETCRACKER@123");
//                headers.put("Content-Type", "application/form-data");
                return headers;
            }

        };
        volleyRequestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
    }
}