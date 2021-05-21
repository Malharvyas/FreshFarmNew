package com.freshfarm.freshfarmnew.Fragments.CategoryFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.freshfarm.freshfarmnew.Adapters.SubCategoryAdapter;
import com.freshfarm.freshfarmnew.Class.BaseUrl;
import com.freshfarm.freshfarmnew.Class.GridSpacing;
import com.freshfarm.freshfarmnew.Model.SubCategory;
import com.freshfarm.freshfarmnew.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;


public class SubCategoryFragment extends Fragment implements SubCategoryAdapter.onClickListener {

    RecyclerView subcategory_recycler;
    RecyclerView.Adapter adapter;
    List<SubCategory> catlist;
    GridLayoutManager gridLayoutManager;
    String url = "", category_id;
    ProgressBar progressBar;
    int c1 = 0;
    GifImageView nodata;

    public SubCategoryFragment() {
        // Required empty public constructor
    }


    public static SubCategoryFragment newInstance(String param1, String param2) {
        SubCategoryFragment fragment = new SubCategoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        catlist = new ArrayList<>();
        adapter = new SubCategoryAdapter(getContext(), catlist, this, "SubCategory");
//        gridLayoutManager = new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sub_category, container, false);
        subcategory_recycler = v.findViewById(R.id.subcategory_recycler);
        progressBar = v.findViewById(R.id.progressbar);

        nodata = v.findViewById(R.id.nodata);


        subcategory_recycler.setHasFixedSize(true);
        subcategory_recycler.setItemAnimator(new DefaultItemAnimator());
        subcategory_recycler.setLayoutManager(new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false));
        subcategory_recycler.setAdapter(adapter);
        subcategory_recycler.addItemDecoration(new GridSpacing(12));

        if (getArguments() != null) {
            category_id = getArguments().getString("category_id");
        }

        progressBar.setVisibility(View.VISIBLE);

        getSubCategory(category_id);

        if (c1 == 1) {
            progressBar.setVisibility(View.GONE);
        }
        return v;
    }


    private void getSubCategory(String category_id) {
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/SubCategory");
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
                                        SubCategory category = new SubCategory();
                                        JSONObject catobj = data.getJSONObject(i);
                                        String cat_id = catobj.getString("category_id");
                                        String sub_cat_id = catobj.getString("subcat_id");
                                        String cat_name = catobj.getString("subcategory_name");
                                        String cat_img = catobj.getString("image");
                                        category.setCategory_id(cat_id);
                                        category.setSubcat_id(sub_cat_id);
                                        category.setSubcategory_name(cat_name);
                                        category.setImage(cat_img);
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
                            if(catlist.isEmpty())
                            {
                                subcategory_recycler.setVisibility(View.GONE);
                                nodata.setVisibility(View.VISIBLE);
                            }
                            else {
                                adapter.notifyDataSetChanged();
                            }
                            c1 = 1;
                            if (c1 == 1) {
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
                params.put("category_id", category_id);
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
    public void onClicked(int position) {
        SubCategory cat = catlist.get(position);
        String cat_id = cat.getCategory_id();
        String sub_cat_id = cat.getSubcat_id();
        Bundle b = new Bundle();
        b.putString("category_id", cat_id);
        b.putString("sub_cat_id", sub_cat_id);
        ProductsFragment p = new ProductsFragment();
        p.setArguments(b);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("products");
        ft.replace(R.id.fragment_container, p);
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
//        progressBar.setVisibility(View.GONE);
//        catlist.clear();
    }
}