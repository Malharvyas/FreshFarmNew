package com.freshfarm.freshfarmnew.Fragments;

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
import com.freshfarm.freshfarmnew.Adapters.CategoryAdapter;
import com.freshfarm.freshfarmnew.Class.BaseUrl;
import com.freshfarm.freshfarmnew.Class.GridSpacing;
import com.freshfarm.freshfarmnew.Fragments.CategoryFragments.SubCategoryFragment;
import com.freshfarm.freshfarmnew.Model.Category;
import com.freshfarm.freshfarmnew.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryFragment extends Fragment implements CategoryAdapter.onClickListener {

    RecyclerView category_recycler;
    RecyclerView.Adapter adapter;
    List<Category> catlist;
    GridLayoutManager gridLayoutManager;
    String url = "";
    ProgressBar progressBar;
    int c1 = 0;

    public CategoryFragment() {
        // Required empty public constructor
    }

    public static CategoryFragment newInstance(String param1, String param2) {
        CategoryFragment fragment = new CategoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        catlist = new ArrayList<>();
        adapter = new CategoryAdapter(getContext(), catlist, this, "Category");
//        gridLayoutManager = new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_category, container, false);
        category_recycler = v.findViewById(R.id.category_recycler);
        progressBar = v.findViewById(R.id.progressbar);


        category_recycler.setHasFixedSize(true);
        category_recycler.setItemAnimator(new DefaultItemAnimator());
        category_recycler.setLayoutManager(new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false));
        category_recycler.setAdapter(adapter);
        category_recycler.addItemDecoration(new GridSpacing(12));

        progressBar.setVisibility(View.VISIBLE);

        getCategory();

        if (c1 == 1) {
            progressBar.setVisibility(View.GONE);
        }
        return v;
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
    public void onResume() {
        super.onResume();
//        progressBar.setVisibility(View.VISIBLE);
//        catlist.clear();
    }
}