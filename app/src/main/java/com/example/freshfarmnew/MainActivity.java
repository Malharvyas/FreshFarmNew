package com.example.freshfarmnew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.freshfarmnew.Class.BaseUrl;
import com.example.freshfarmnew.Fragments.AboutUsFragment;
import com.example.freshfarmnew.Fragments.CartFragment;
import com.example.freshfarmnew.Fragments.CategoryFragment;
import com.example.freshfarmnew.Fragments.CategoryFragments.SubCategoryFragment;
import com.example.freshfarmnew.Fragments.ContactUsFragment;
import com.example.freshfarmnew.Fragments.HomeFragment;
import com.example.freshfarmnew.Fragments.MyOrdersFragment;
import com.example.freshfarmnew.Fragments.ProfileFragment;
import com.example.freshfarmnew.Fragments.ProfileFragments.ReferralFragment;
import com.example.freshfarmnew.Fragments.SearchResultsFragment;
import com.example.freshfarmnew.Fragments.WalletFragment;
import com.example.freshfarmnew.Fragments.WishlistFragment;
import com.example.freshfarmnew.Model.Product;
import com.example.freshfarmnew.Model.ProductVariation;
import com.example.freshfarmnew.Model.SubCategory;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonArray;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultListener;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, PaymentResultWithDataListener {

    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ImageView navprofile, nav_location;
    BottomNavigationView bottomNavigationView;
    Boolean isLogin;
    String cus_id = "", url = "", address = "";
    EditText search;
    private static final int REQUEST_CODE = 101;
    TextView head_address;
    View headerView;
    private static final int PERMISSION_REQUEST_CODE = 1;
    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                // Code for above or equal 23 API Oriented Device
                // Your Permission granted already .Do next code
            } else {
                requestPermission(); // Code for permission
            }
//        }

        navigationView = findViewById(R.id.nav_view);
        search = findViewById(R.id.search_input);
        headerView = navigationView.getHeaderView(0);
        head_address = headerView.findViewById(R.id.head_address);

        SharedPreferences sharedPreferences = getSharedPreferences("userlogin", Context.MODE_PRIVATE);
        isLogin = sharedPreferences.getBoolean("islogin", false);

        if (isLogin == false) {
            navigationView.inflateMenu(R.menu.side_nav_logout);
        } else {
            navigationView.inflateMenu(R.menu.side_nav_login);
        }

        SharedPreferences sharedPreferences2 = getSharedPreferences("userpref", Context.MODE_PRIVATE);
        cus_id = sharedPreferences2.getString("customer_id", "");
        address = sharedPreferences2.getString("address", "");

        head_address.setText(address);

        if (isLogin == true && address.equals(""))
        {
            finish();
            startActivity(new Intent(getApplicationContext(),MapsActivity.class));
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String ss = search.getText().toString();
                    if (ss == null || ss.equals("")) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        Toast.makeText(getApplicationContext(), "Please enter a valid search input", Toast.LENGTH_SHORT).show();
                        search.clearFocus();
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("search_key", ss);
                        SearchResultsFragment srf = new SearchResultsFragment();
                        srf.setArguments(bundle);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("searchresults");
                        ft.replace(R.id.fragment_container, srf);
                        ft.commit();
                    }
                    return true;
                }
                return false;
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        navprofile = findViewById(R.id.prof_icon);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        nav_location = findViewById(R.id.nav_location);
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        navprofile.setOnClickListener(this);
        nav_location.setOnClickListener(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
                switch (item.getItemId()) {
                    case R.id.nav_wallet: {
                        SharedPreferences sharedPreferences = getSharedPreferences("userlogin", Context.MODE_PRIVATE);
                        Boolean islogin = sharedPreferences.getBoolean("islogin", false);
                        if (islogin == false) {
//                            bottomNavigationView.getMenu().setGroupCheckable(0, false, true);
                            bottomNavigationView.setSelectedItemId(R.id.nav_home);
                            Toast.makeText(getApplicationContext(), "Please login to continue", Toast.LENGTH_SHORT).show();
                        } else {
                            WalletFragment w = new WalletFragment();
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("wallet");
                            ft.replace(R.id.fragment_container, w);
                            ft.commit();
                        }
                    }
                    break;
                    case R.id.nav_category: {
                        CategoryFragment c = new CategoryFragment();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("category");
                        ft.replace(R.id.fragment_container, c);
                        ft.commit();
                    }
                    break;
                    case R.id.nav_home: {
                        HomeFragment h = new HomeFragment();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("home");
                        ft.replace(R.id.fragment_container, h);
                        ft.commit();
                    }
                    break;
                    case R.id.nav_cart: {
                        SharedPreferences sharedPreferences = getSharedPreferences("userlogin", Context.MODE_PRIVATE);
                        Boolean islogin = sharedPreferences.getBoolean("islogin", false);
                        if (islogin == false) {
                            bottomNavigationView.setSelectedItemId(R.id.nav_home);
                            Toast.makeText(getApplicationContext(), "Please login to continue", Toast.LENGTH_SHORT).show();
                        } else {
                            CartFragment c = new CartFragment();
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("cart");
                            ft.replace(R.id.fragment_container, c);
                            ft.commit();
                        }

                    }
                    break;
                    case R.id.nav_account: {
                        SharedPreferences sharedPreferences = getSharedPreferences("userlogin", Context.MODE_PRIVATE);
                        Boolean islogin = sharedPreferences.getBoolean("islogin", false);
                        if (islogin == false) {
                            bottomNavigationView.setSelectedItemId(R.id.nav_home);
//                            getSupportFragmentManager().popBackStack();
//                            bottomNavigationView.getMenu().getItem(1).setChecked(true);
                            startActivity(new Intent(getApplicationContext(), login.class));
                        } else {
                            ProfileFragment p = new ProfileFragment();
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("profile");
                            ft.replace(R.id.fragment_container, p);
                            ft.commit();
                        }

                    }
                    break;
                }
                return true;
            }
        });


        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    cartnum(cus_id);
                }
            }
        });
        thread.start();

        if (savedInstanceState == null) {
            HomeFragment h = new HomeFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("home");
            ft.replace(R.id.fragment_container, h);
            ft.commit();
        }


    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    private void cartnum(String cus_id) {
        BaseUrl b = new BaseUrl();
        url = b.url;
        url = url.concat("freshfarm/api/ApiController/cartdata");
        RequestQueue volleyRequestQueue = Volley.newRequestQueue(getApplicationContext());

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
                                JSONObject json2 = json.getJSONObject("cartdata");
                                Boolean status = json2.getBoolean("status");
                                String stat = status.toString();
                                if (stat.equals("true")) {
//                                    Toast.makeText(getApplicationContext(),"Hello",Toast.LENGTH_SHORT).show();
                                    JSONArray data = json2.getJSONArray("data");
                                    int count = data.length();
//                                    Toast.makeText(getApplicationContext(),""+count,Toast.LENGTH_SHORT).show();
                                    BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.nav_cart);
                                    badgeDrawable.setNumber(count);
                                    badgeDrawable.setBackgroundColor(Color.parseColor("#FFA823"));
                                    badgeDrawable.setBadgeTextColor(Color.parseColor("#FFFFFF"));
                                } else if (stat.equals("false")) {
                                    String msg = json2.getString("Message");
//                                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                }
//                                Toast.makeText(getContext(),""+catlist.size(),Toast.LENGTH_SHORT).show();
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
//                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
//                    Toast.makeText(getApplicationContext(), "Error : " + error, Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("customer_id", cus_id);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        bottomNavigationView.getMenu().setGroupCheckable(0, false, true);
        switch (item.getItemId()) {
            case R.id.nav_side_profile: {
                bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
                bottomNavigationView.getMenu().getItem(4).setChecked(true);
                ProfileFragment p = new ProfileFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("profile");
                ft.replace(R.id.fragment_container, p);
                ft.commit();
            }
            break;
            case R.id.nav_side_my_orders: {
                MyOrdersFragment mo = new MyOrdersFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("orders");
                ft.replace(R.id.fragment_container, mo);
                ft.commit();
            }
            break;
            case R.id.nav_side_my_wallet: {
                bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
                bottomNavigationView.getMenu().getItem(2).setChecked(true);
                WalletFragment w = new WalletFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("wallet");
                ft.replace(R.id.fragment_container, w);
                ft.commit();
            }
            break;
            case R.id.nav_side_my_wishlist: {
                WishlistFragment w = new WishlistFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("wishlist");
                ft.replace(R.id.fragment_container, w);
                ft.commit();
            }
            break;
            case R.id.nav_side_contact: {
                ContactUsFragment c = new ContactUsFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("contact");
                ft.replace(R.id.fragment_container, c);
                ft.commit();
            }
            break;
            case R.id.nav_about2: {
                AboutUsFragment a = new AboutUsFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("about");
                ft.replace(R.id.fragment_container, a);
                ft.commit();
            }
            break;
            case R.id.nav_contact: {
                ContactUsFragment c = new ContactUsFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("contact");
                ft.replace(R.id.fragment_container, c);
                ft.commit();
            }
            break;
            case R.id.nav_about: {
                AboutUsFragment a = new AboutUsFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("about");
                ft.replace(R.id.fragment_container, a);
                ft.commit();
            }
            break;
            case R.id.nav_side_logout: {
                SharedPreferences sharedPreferences = getSharedPreferences("userlogin", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("islogin", false);
                editor.apply();
                SharedPreferences sharedPreferences1 = getSharedPreferences("userpref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                editor1.clear();
                editor1.apply();
                SharedPreferences sharedPreferences2 = getSharedPreferences("cartdetails", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                editor2.clear();
                editor2.apply();
                SharedPreferences sharedPreferences3 = getSharedPreferences("temp", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor3 = sharedPreferences3.edit();
                editor3.clear();
                editor3.apply();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
            break;
            case R.id.nav_login3: {
                startActivity(new Intent(getApplicationContext(), login.class));
            }
            break;
            case R.id.nav_side_referral: {
                ReferralFragment rf = new ReferralFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("referral");
                ft.replace(R.id.fragment_container, rf);
                ft.commit();
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//            getSupportFragmentManager().popBackStack();
            if (getSupportFragmentManager().getBackStackEntryCount() - 1 >= 0) {
                if (getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName() != null) {
                    if (getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals("category")) {
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        HomeFragment h = new HomeFragment();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("home");
                        ft.replace(R.id.fragment_container, h);
                        ft.commit();
                    } else if (getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals("wallet")) {
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        HomeFragment h = new HomeFragment();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("home");
                        ft.replace(R.id.fragment_container, h);
                        ft.commit();
                    } else if (getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals("cart")) {
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        HomeFragment h = new HomeFragment();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("home");
                        ft.replace(R.id.fragment_container, h);
                        ft.commit();
                    } else if (getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals("profile")) {
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        HomeFragment h = new HomeFragment();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("home");
                        ft.replace(R.id.fragment_container, h);
                        ft.commit();
                    } else if (getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals("orders")) {
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        HomeFragment h = new HomeFragment();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("home");
                        ft.replace(R.id.fragment_container, h);
                        ft.commit();
                    } else if (getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals("wishlist")) {
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        HomeFragment h = new HomeFragment();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("home");
                        ft.replace(R.id.fragment_container, h);
                        ft.commit();
                    } else if (getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals("contact")) {
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        HomeFragment h = new HomeFragment();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("home");
                        ft.replace(R.id.fragment_container, h);
                        ft.commit();
                    } else if (getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals("about")) {
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        HomeFragment h = new HomeFragment();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("home");
                        ft.replace(R.id.fragment_container, h);
                        ft.commit();
                    } else if (getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals("searchresults")) {
                        search.setText("");
                        search.clearFocus();
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        HomeFragment h = new HomeFragment();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("home");
                        ft.replace(R.id.fragment_container, h);
                        ft.commit();
                    } else if (getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals("home")) {
                        finish();
                    } else {
                        super.onBackPressed();
                    }
                } else {
                    super.onBackPressed();
                }
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.prof_icon: {
                SharedPreferences sharedPreferences = getSharedPreferences("userlogin", Context.MODE_PRIVATE);
                Boolean islogin = sharedPreferences.getBoolean("islogin", false);
                if (islogin == false) {
//                            getSupportFragmentManager().popBackStack();
//                            bottomNavigationView.getMenu().getItem(1).setChecked(true);
                    startActivity(new Intent(getApplicationContext(), login.class));
                } else {
                    bottomNavigationView.getMenu().getItem(4).setChecked(true);
                    ProfileFragment p = new ProfileFragment();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("profile");
                    ft.replace(R.id.fragment_container, p);
                    ft.commit();
                }
            }
            break;

            case R.id.nav_location: {
                if (checkPermission()) {
                    SharedPreferences sharedPreferences = getSharedPreferences("userlogin", Context.MODE_PRIVATE);
                    Boolean islogin = sharedPreferences.getBoolean("islogin", false);
                    if (islogin == false) {
                        Toast.makeText(getApplicationContext(), "Please login to continue", Toast.LENGTH_SHORT).show();
                    } else {
                        gotomaps();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please provide location permission to continue", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
            }
            break;
        }
    }

    private void gotomaps() {
        Intent i = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(i);
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        Checkout.clearUserData(getApplicationContext());
        try {
            if (getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals("wallet")) {
                SharedPreferences sharedPreferences = getSharedPreferences("payment_details", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("wallet_payment", "1");
                editor.putString("wpstatus", "1");
                editor.apply();
            } else if (getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals("PlaceOrder")) {
                SharedPreferences sharedPreferences = getSharedPreferences("payment_details", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("cart_payment", "1");
                editor.putString("cpstatus", "1");
                editor.apply();
            }
        } catch (Exception e) {
            Log.e("Exception..", "" + e);
        }

    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Checkout.clearUserData(getApplicationContext());
        SharedPreferences sharedPreferences = getSharedPreferences("payment_details", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("wallet_payment", "1");
        editor.putString("wpstatus", "0");
        editor.apply();
        switch (i) {
            case Checkout.NETWORK_ERROR: {
                Toast.makeText(getApplicationContext(), "There was an network error..please try after sometime", Toast.LENGTH_SHORT).show();
            }
            break;
            case Checkout.INVALID_OPTIONS: {
                Toast.makeText(getApplicationContext(), "User data is incorrect", Toast.LENGTH_SHORT).show();
            }
            break;
            case Checkout.PAYMENT_CANCELED: {
                Toast.makeText(getApplicationContext(), "Canceled!!", Toast.LENGTH_SHORT).show();
            }
            break;
            case Checkout.TLS_ERROR: {
                Toast.makeText(getApplicationContext(), "This device is not supported for online payment", Toast.LENGTH_SHORT).show();
            }
            break;
            default:
                Toast.makeText(getApplicationContext(), "Error : " + s, Toast.LENGTH_SHORT).show();
        }
    }


//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        finish();
//        overridePendingTransition(0, 0);
//        startActivity(getIntent());
//        overridePendingTransition(0, 0);
//    }

    @Override
    protected void onPause() {
        super.onPause();
//        thread.interrupt();
//        thread = null;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        thread.start();
    }
}