package com.example.freshfarmnew.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Base64;
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
import com.example.freshfarmnew.Adapters.BannerAdapter;
import com.example.freshfarmnew.Adapters.CategoryAdapter;
import com.example.freshfarmnew.Adapters.DealsAdapter;
import com.example.freshfarmnew.Adapters.TrendingAdapter;
import com.example.freshfarmnew.Class.BaseUrl;
import com.example.freshfarmnew.Class.GridSpacing;
import com.example.freshfarmnew.Fragments.CategoryFragments.SubCategoryFragment;
import com.example.freshfarmnew.MainActivity;
import com.example.freshfarmnew.Model.Category;
import com.example.freshfarmnew.Model.Product;
import com.example.freshfarmnew.Model.ProductVariation;
import com.example.freshfarmnew.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

public class HomeFragment extends Fragment implements CategoryAdapter.onClickListener, TrendingAdapter.onClickListener, DealsAdapter.onClickListener {

    ViewPager viewPager;
    String url = "";
    ArrayList<String> banners = new ArrayList<String>();
    BannerAdapter bannerAdapter;
    Timer timer;
    CircleIndicator circleIndicator;
    RecyclerView category_recycler, trending_recycler, deals_recycler;
    RecyclerView.Adapter adapter, adapter2, adapter3;
    List<Category> catlist;
    List<ProductVariation> variantionlist, variantionlist2;
    List<Product> prolist, prolist2;
    String cat_id = "0", sub_cat_id = "0", trend_id = "3", deals_id = "2";
    int c1 = 0, c2 = 0, c3 = 0, c4 = 0;
    String cus_id = "";

    ProgressBar progressBar;

    GridLayoutManager gridLayoutManager, gridLayoutManager2, gridLayoutManager3;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        catlist = new ArrayList<>();
        prolist = new ArrayList<>();
        prolist2 = new ArrayList<>();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userpref", Context.MODE_PRIVATE);
        cus_id = sharedPreferences.getString("customer_id", "");

        adapter = new CategoryAdapter(getContext(), catlist, this, "Home");
        adapter2 = new TrendingAdapter(getContext(), prolist, this, cus_id);
        adapter3 = new DealsAdapter(getContext(), prolist2, this);

//        gridLayoutManager = new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false);
//        gridLayoutManager2 = new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false);
//        gridLayoutManager3 = new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        viewPager = v.findViewById(R.id.banner1);
        circleIndicator = v.findViewById(R.id.circle_indicator);
        category_recycler = v.findViewById(R.id.cat_recycler);
        trending_recycler = v.findViewById(R.id.trending_recycler);
        deals_recycler = v.findViewById(R.id.deals_recycler);
        progressBar = v.findViewById(R.id.progressbar);

        category_recycler.setHasFixedSize(true);
        category_recycler.setItemAnimator(new DefaultItemAnimator());
        category_recycler.setLayoutManager(new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false));
        category_recycler.setAdapter(adapter);
        category_recycler.addItemDecoration(new GridSpacing(12));

        trending_recycler.setHasFixedSize(true);
        trending_recycler.setItemAnimator(new DefaultItemAnimator());
        trending_recycler.setLayoutManager(new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false));
        trending_recycler.setAdapter(adapter2);
        trending_recycler.addItemDecoration(new GridSpacing(12));

        deals_recycler.setHasFixedSize(true);
        deals_recycler.setItemAnimator(new DefaultItemAnimator());
        deals_recycler.setLayoutManager(new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false));
        deals_recycler.setAdapter(adapter3);
        deals_recycler.addItemDecoration(new GridSpacing(12));

        progressBar.setVisibility(View.VISIBLE);

        getBanners();

        getCategory();

        gettrending(cat_id, sub_cat_id, trend_id);

        getdeals(cat_id, sub_cat_id, deals_id);

        if (c1 == 1 && c2 == 1 && c3 == 1 && c4 == 1) {
            progressBar.setVisibility(View.GONE);
        }

        return v;
    }

    private void getdeals(String cat_id, String sub_cat_id, String deals_id) {
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/getProduct");
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        BaseUrl b = new BaseUrl();
                        url = b.url;
                        if (response != null) {
                            JSONObject json = null;

                            try {
                                json = new JSONObject(String.valueOf(response));
                                JSONObject json2 = json.getJSONObject("getProduct");
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                if (stat.equals("true")) {
                                    prolist2.clear();
                                    JSONArray data = json2.getJSONArray("data");
                                    for (int i = 0; i < data.length(); i++) {
                                        Product product = new Product();
                                        JSONObject catobj = data.getJSONObject(i);
                                        String pro_id = catobj.getString("product_id");
                                        String pro_name = catobj.getString("product_name");
                                        String pro_img = catobj.getString("product_image");
                                        variantionlist = new ArrayList<>();
                                        JSONArray variant = catobj.getJSONArray("variation");
                                        for (int j = 0; j < variant.length(); j++) {
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
                                        prolist2.add(product);
                                    }

                                } else if (stat.equals("false")) {
                                    String msg = json2.getString("Message");
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                }
//                                Toast.makeText(getContext(),""+catlist.size(),Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            adapter3.notifyDataSetChanged();
                            c4 = 1;
                            if (c1 == 1 && c2 == 1 && c3 == 1 && c4 == 1) {
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                BaseUrl b = new BaseUrl();
                url = b.url;
                if (error instanceof ClientError) {
                    try {
                        String responsebody = new String(error.networkResponse.data, "utf-8");
                        JSONObject data = new JSONObject(responsebody);
                        Boolean status = data.getBoolean("status");
                        String stat = status.toString();
                        if (stat.equals("false")) {
                            String msg = data.getString("Message");
                            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "Error : " + error, Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("category_id", cat_id);
                params.put("subcat_id", sub_cat_id);
                params.put("product_type", deals_id);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
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

    private void gettrending(String cat_id, String sub_cat_id, String trend_id) {
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/getProduct");
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        BaseUrl b = new BaseUrl();
                        url = b.url;
                        if (response != null) {
                            JSONObject json = null;

                            try {
                                json = new JSONObject(String.valueOf(response));
                                JSONObject json2 = json.getJSONObject("getProduct");
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                if (stat.equals("true")) {
                                    prolist.clear();
                                    JSONArray data = json2.getJSONArray("data");
                                    for (int i = 0; i < data.length(); i++) {
                                        Product product = new Product();
                                        JSONObject catobj = data.getJSONObject(i);
                                        String pro_id = catobj.getString("product_id");
                                        String pro_name = catobj.getString("product_name");
                                        String pro_img = catobj.getString("product_image");
                                        variantionlist2 = new ArrayList<>();
                                        JSONArray variant = catobj.getJSONArray("variation");
                                        for (int j = 0; j < variant.length(); j++) {
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
                                            variantionlist2.add(pv);
                                        }
                                        product.setProduct_id(pro_id);
                                        product.setProduct_name(pro_name);
                                        product.setProduct_image(pro_img);
                                        product.setVariations(variantionlist2);
                                        prolist.add(product);
                                    }

                                } else if (stat.equals("false")) {
                                    String msg = json2.getString("Message");
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                }
//                                Toast.makeText(getContext(),""+catlist.size(),Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            adapter2.notifyDataSetChanged();
                            c3 = 1;
                            if (c1 == 1 && c2 == 1 && c3 == 1 && c4 == 1) {
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                BaseUrl b = new BaseUrl();
                url = b.url;
                if (error instanceof ClientError) {
                    try {
                        String responsebody = new String(error.networkResponse.data, "utf-8");
                        JSONObject data = new JSONObject(responsebody);
                        Boolean status = data.getBoolean("status");
                        String stat = status.toString();
                        if (stat.equals("false")) {
                            String msg = data.getString("Message");
                            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "Error : " + error, Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("category_id", cat_id);
                params.put("subcat_id", sub_cat_id);
                params.put("product_type", trend_id);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
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

    private void getCategory() {
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/category");
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        BaseUrl b = new BaseUrl();
                        url = b.url;
                        if (response != null) {
                            JSONObject json = null;

                            try {
                                json = new JSONObject(String.valueOf(response));
                                JSONObject json2 = json.getJSONObject("CatData");
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                if (stat.equals("true")) {
                                    catlist.clear();
                                    JSONArray data = json2.getJSONArray("data");
                                    for (int i = 0; i < data.length(); i++) {
                                        Category category = new Category();
                                        JSONObject catobj = data.getJSONObject(i);
                                        String cat_id = catobj.getString("category_id");
                                        String cat_name = catobj.getString("category_name");
                                        String cat_img = catobj.getString("category_image");
                                        category.setCategory_id(cat_id);
                                        category.setCategory_name(cat_name);
                                        category.setCategory_image(cat_img);
                                        catlist.add(category);
                                    }

                                } else if (stat.equals("false")) {
                                    String msg = json2.getString("Message");
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                }
//                                Toast.makeText(getContext(),""+catlist.size(),Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            adapter.notifyDataSetChanged();
                            c2 = 1;
                            if (c1 == 1 && c2 == 1 && c3 == 1 && c4 == 1) {
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                BaseUrl b = new BaseUrl();
                url = b.url;
                if (error instanceof ClientError) {
                    try {
                        String responsebody = new String(error.networkResponse.data, "utf-8");
                        JSONObject data = new JSONObject(responsebody);
                        Boolean status = data.getBoolean("status");
                        String stat = status.toString();
                        if (stat.equals("false")) {
                            String msg = data.getString("Message");
                            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "Error : " + error, Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
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

    private void getBanners() {
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/banner");
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        BaseUrl b = new BaseUrl();
                        url = b.url;
                        if (response != null) {
                            JSONObject json = null;

                            try {
                                json = new JSONObject(String.valueOf(response));
                                JSONObject json2 = json.getJSONObject("BannerData");
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                if (stat.equals("true")) {
                                    banners.clear();
                                    JSONArray data = json2.getJSONArray("data");
                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject imgobj = data.getJSONObject(i);
                                        String bimg = imgobj.getString("banner_image");
                                        banners.add(bimg);
                                    }
                                    bannerAdapter = new BannerAdapter(getContext(), banners);
                                    viewPager.setAdapter(bannerAdapter);
                                    circleIndicator.setViewPager(viewPager);
                                    c1 = 1;
                                    if (c1 == 1 && c2 == 1 && c3 == 1 && c4 == 1) {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    TimerTask timerTask = new TimerTask() {
                                        @Override
                                        public void run() {
                                            viewPager.post(new Runnable() {

                                                @Override
                                                public void run() {
                                                    if (banners.size() > 0) {
                                                        viewPager.setCurrentItem((viewPager.getCurrentItem() + 1) % banners.size());
                                                    }
                                                }
                                            });
                                        }
                                    };
                                    timer = new Timer();
                                    timer.schedule(timerTask, 6000, 6000);
                                } else if (stat.equals("false")) {
                                    String msg = json2.getString("Message");
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
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
                if (error instanceof ClientError) {
                    try {
                        String responsebody = new String(error.networkResponse.data, "utf-8");
                        JSONObject data = new JSONObject(responsebody);
                        Boolean status = data.getBoolean("status");
                        String stat = status.toString();
                        if (stat.equals("false")) {
                            String msg = data.getString("Message");
                            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "Error : " + error, Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
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
    public void onClicked(int position) {
        Category cat = catlist.get(position);
        String cat_id = cat.getCategory_id();
        Bundle b = new Bundle();
        b.putString("category_id", cat_id);
        SubCategoryFragment s = new SubCategoryFragment();
        s.setArguments(b);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("sub_category");
        ft.replace(R.id.fragment_container, s);
        ft.commit();
    }

    @Override
    public void onDealsClicked(int position) {
        Product p = prolist2.get(position);
        String p_id = p.getProduct_id();
        Bundle b = new Bundle();
        b.putString("product_id", p_id);
        ProductDetailsFragment pdf = new ProductDetailsFragment();
        pdf.setArguments(b);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("product_details");
        ft.replace(R.id.fragment_container, pdf);
        ft.commit();
    }

    @Override
    public void onDAddClicked(int position, String quantity, String product_id, String v_id) {
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
                        if (response != null) {
                            JSONObject json = null;

                            try {
                                json = new JSONObject(String.valueOf(response));
                                JSONObject json2 = json.getJSONObject("addtocart");
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                if (stat.equals("true")) {
                                    progressBar.setVisibility(View.GONE);
                                    if (quantity.equals("1")) {
                                        String msg = json2.getString("Message");
                                        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                    }
                                    if (quantity.equals("0")) {
                                        String msg = "Item Removed Successfully";
                                        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                    }

                                } else if (stat.equals("false")) {
                                    progressBar.setVisibility(View.GONE);
                                    String msg = json2.getString("Message");
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
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
                if (error instanceof ClientError) {
                    try {
                        String responsebody = new String(error.networkResponse.data, "utf-8");
                        JSONObject data = new JSONObject(responsebody);
                        Boolean status = data.getBoolean("status");
                        String stat = status.toString();
                        if (stat.equals("false")) {
                            String msg = data.getString("Message");
                            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "Error : " + error, Toast.LENGTH_SHORT).show();
                }
            }
        }) {
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
            public Map<String, String> getHeaders() throws AuthFailureError {
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
    public void onTrendClicked(int position) {
        Product p = prolist.get(position);
        String p_id = p.getProduct_id();
        Bundle b = new Bundle();
        b.putString("product_id", p_id);
        ProductDetailsFragment pdf = new ProductDetailsFragment();
        pdf.setArguments(b);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("product_details");
        ft.replace(R.id.fragment_container, pdf);
        ft.commit();
    }

    @Override
    public void onTAddClicked(int position, String quantity, String product_id, String v_id) {
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
                        if (response != null) {
                            JSONObject json = null;

                            try {
                                json = new JSONObject(String.valueOf(response));
                                JSONObject json2 = json.getJSONObject("addtocart");
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                if (stat.equals("true")) {
                                    progressBar.setVisibility(View.GONE);
                                    if (quantity.equals("1")) {
                                        String msg = json2.getString("Message");
                                        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                    }
                                    if (quantity.equals("0")) {
                                        String msg = "Item Removed Successfully";
                                        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                    }

                                } else if (stat.equals("false")) {
                                    progressBar.setVisibility(View.GONE);
                                    String msg = json2.getString("Message");
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
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
                if (error instanceof ClientError) {
                    try {
                        String responsebody = new String(error.networkResponse.data, "utf-8");
                        JSONObject data = new JSONObject(responsebody);
                        Boolean status = data.getBoolean("status");
                        String stat = status.toString();
                        if (stat.equals("false")) {
                            String msg = data.getString("Message");
                            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "Error : " + error, Toast.LENGTH_SHORT).show();
                }
            }
        }) {
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
            public Map<String, String> getHeaders() throws AuthFailureError {
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
    public void onResume() {
        super.onResume();
//        progressBar.setVisibility(View.VISIBLE);
//        catlist.clear();
//        prolist.clear();
//        prolist2.clear();
//        banners.clear();
    }

    @Override
    public void onPause() {
        super.onPause();
//        catlist.clear();
//        prolist.clear();
//        prolist2.clear();
//        banners.clear();
//        adapter.notifyDataSetChanged();
//        adapter2.notifyDataSetChanged();
//        adapter3.notifyDataSetChanged();
//        bannerAdapter.notifyDataSetChanged();

    }
}