package com.freshfarm.freshfarmnew.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.freshfarm.freshfarmnew.Adapters.ProductImageAdapter;
import com.freshfarm.freshfarmnew.Class.BaseUrl;
import com.freshfarm.freshfarmnew.Model.Product;
import com.freshfarm.freshfarmnew.Model.ProductVariation;
import com.freshfarm.freshfarmnew.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;


public class ProductDetailsFragment extends Fragment {

    String url = "", product_id, cus_id;
    ViewPager viewPager;
    CircleIndicator circleIndicator;
    TextView productname, productprice, productdesc;
    Spinner pdvariant;
    Button pdbuy, pdadd;
    List<Product> prolist;
    List<ProductVariation> variantionlist;
    ArrayList<String> stringArrayList = new ArrayList<String>();
    ArrayList<String> imageslist = new ArrayList<String>();
    ProductImageAdapter productImageAdapter;
    ProgressBar progressBar;

    public ProductDetailsFragment() {
        // Required empty public constructor
    }


    public static ProductDetailsFragment newInstance(String param1, String param2) {
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prolist = new ArrayList<>();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userpref", Context.MODE_PRIVATE);
        cus_id = sharedPreferences.getString("customer_id", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product_details, container, false);
        viewPager = v.findViewById(R.id.pd_images);
        circleIndicator = v.findViewById(R.id.circle_indicator);
        productname = v.findViewById(R.id.pdproduct_name);
        productprice = v.findViewById(R.id.pdproduct_price);
        productdesc = v.findViewById(R.id.pdproduct_description);
        pdvariant = v.findViewById(R.id.pd_variation);
        pdbuy = v.findViewById(R.id.pd_buy);
        pdadd = v.findViewById(R.id.pd_add);
        progressBar = v.findViewById(R.id.progressbar);

        if (getArguments() != null) {
            product_id = getArguments().getString("product_id");
        }

        getproductdetails(product_id);

        pdbuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("printLog", "========pdbuy click=========");
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("cartdetails", Context.MODE_PRIVATE);
                String amount = sharedPreferences.getString("totleAmount", "0");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("fragment", "productd");
                editor.apply();
                Bundle bundle = new Bundle();
                bundle.putString("amount", amount);
                bundle.putString("total_items", "1");
                Log.e("PrintLog", "--final_amount--" + amount);
                CheckoutFragment checkoutFragment = new CheckoutFragment();
                checkoutFragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("Cart");
                ft.replace(R.id.fragment_container, checkoutFragment);
                ft.commit();
            }
        });

        pdadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("pdtemp", Context.MODE_PRIVATE);
                String v_id = sharedPreferences.getString("v_id", "0");
                addtocart(v_id);
            }
        });
        return v;
    }

    private void addtocart(String v_id) {
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
                                    String msg = json2.getString("Message");
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

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
                params.put("quantity", "1");
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

    private void getproductdetails(String product_id) {
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/productDetails");
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("printLog", "=========onResponse=========" + response);
                        BaseUrl b = new BaseUrl();
                        url = b.url;
                        if (response != null) {
                            JSONObject json = null;

                            try {
                                json = new JSONObject(String.valueOf(response));
                                JSONObject json2 = json.getJSONObject("cartdata");
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
                                        String cat_id = catobj.getString("category_id");
                                        String sub_cat_id = catobj.getString("subcat_id");
                                        String pro_desc = catobj.getString("product_discription");
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
                                        imageslist = new ArrayList<>();
                                        JSONArray images = catobj.getJSONArray("product_image");
                                        for (int k = 0; k < images.length(); k++) {
                                            JSONObject imgobj = images.getJSONObject(k);
                                            String img = imgobj.getString("product_image");
                                            imageslist.add(img);
                                        }
                                        product.setProduct_id(pro_id);
                                        product.setProduct_name(pro_name);
                                        product.setVariations(variantionlist);
                                        product.setCategory_id(cat_id);
                                        product.setSubcat_id(sub_cat_id);
                                        product.setProduct_discription(pro_desc);
                                        prolist.add(product);
                                    }
                                    for (int i = 0; i < variantionlist.size(); i++) {
                                        ProductVariation variation = variantionlist.get(i);
                                        String unit_val = variation.getUnit_val();
                                        String unit = variation.getUnit();
                                        String combine = unit_val + " " + unit;
                                        stringArrayList.add(combine);
                                    }

                                    productImageAdapter = new ProductImageAdapter(getContext(), imageslist);
                                    viewPager.setAdapter(productImageAdapter);
                                    circleIndicator.setViewPager(viewPager);

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_text, stringArrayList);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    pdvariant.setAdapter(adapter);
                                    Product p = prolist.get(0);
                                    productname.setText(p.getProduct_name());
                                    productdesc.setText(p.getProduct_discription());

                                    pdvariant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                            for (int j = 0; j < stringArrayList.size(); j++) {
                                                ProductVariation pv = variantionlist.get(j);
                                                String item = pv.getUnit_val() + " " + pv.getUnit();
                                                String selected = pdvariant.getSelectedItem().toString();
                                                if (item.equals(selected)) {
                                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("pdtemp", Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString("v_id", pv.getV_id());
                                                    editor.apply();
                                                    productprice.setText("\u20B9 " + pv.getPrice());
                                                    savedetailstopref(prolist);
                                                    Log.e("printLog","----ProductPrice----"+pv.getPrice());
                                                }
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });


                                } else if (stat.equals("false")) {
                                    String msg = json2.getString("Message");
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                }
//                                Toast.makeText(getContext(),""+catlist.size(),Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//                            adapter3.notifyDataSetChanged();
//                            c4 = 1;
//                            if(c1 == 1 && c2 == 1 && c3 == 1 && c4 ==1)
//                            {
//                                progressBar.setVisibility(View.GONE);
//                            }
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
                params.put("product_id", product_id);
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

    private void savedetailstopref(List<Product> prolist) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("cartdetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        List<String> productquantity2 = new ArrayList<>();
        List<String> productprice2 = new ArrayList<>();
        List<String> productid2 = new ArrayList<>();
        List<String> productcategoryid2 = new ArrayList<>();

        String price = productprice.getText().toString();
        Log.e("printLog", "===price===" + price);
        char c = '\0';
        StringBuffer sf = new StringBuffer();
        for (int i = 0; i < price.length(); i++) {
            c = price.charAt(i);
            if (c == '\u20B9') {

            } else {
                sf.append(c);
            }
        }

        String newprice = String.valueOf(sf);
        Log.e("printLog", "=====newprice=====" + newprice);
//        Toast.makeText(getContext(),""+price,Toast.LENGTH_SHORT).show();
        if (prolist != null) {
            for (int i = 0; i < prolist.size(); i++) {
                productid2.add(prolist.get(i).getProduct_id());
                productprice2.add(newprice);
                productquantity2.add("1");
                productcategoryid2.add(prolist.get(i).getCategory_id());
            }
        }

        Gson gson = new Gson();
        String pidset = null, ppriceset = null, pquantset = null, pcategoryset = null;
        if (productid2 != null) {
            pidset = gson.toJson(productid2);
            Log.e("printLog", "=====pidset=====" + pidset);
        }
        if (productprice2 != null) {
            ppriceset = gson.toJson(productprice2);
            Log.e("printLog", "=====ppriceset=====" + ppriceset);
        }
        if (productquantity2 != null) {
            pquantset = gson.toJson(productquantity2);
            Log.e("printLog", "=====pquantset=====" + pquantset);
        }
        if (productcategoryid2 != null) {
            pcategoryset = gson.toJson(productcategoryid2);
            Log.e("printLog", "=====pcategoryset=====" + pcategoryset);
        }

        editor.putString("pidset", pidset);
        editor.putString("ppriceset", ppriceset);
        editor.putString("pquantset", pquantset);
        editor.putString("totleAmount", newprice);
        editor.putString("pcatset", pcategoryset);
        editor.apply();

    }
}