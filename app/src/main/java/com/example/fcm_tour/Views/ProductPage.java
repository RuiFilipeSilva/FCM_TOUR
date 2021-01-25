package com.example.fcm_tour.Views;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fcm_tour.API;
import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProductPage extends Fragment {
    Bundle bundle;
    static View v;
    static ImageView imgView;
    static TextView nameTxt, priceTxt, descTxt;
    static Button goBackBtn;
    LinearLayout addToCartBtn, removeFromCartBtn;
    static String productId, productName, productPrice, productDesc, productImg, productState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_product_page, container, false);
        bundle = this.getArguments();
        nameTxt = v.findViewById(R.id.title);
        priceTxt = v.findViewById(R.id.price);
        descTxt = v.findViewById(R.id.txt);
        imgView = v.findViewById(R.id.productImg);
        goBackBtn = v.findViewById(R.id.goBackBtn);
        goBackBtn.setOnClickListener(v -> goBackToCatalog());
        addToCartBtn = v.findViewById(R.id.addToCartBtn);
        addToCartBtn.setOnClickListener(v -> {
            addToCart(productId, getContext());
        });
        removeFromCartBtn = v.findViewById(R.id.removeFromCartBtn);
        removeFromCartBtn.setOnClickListener(v -> {
            removeFromCart(productId, getContext());
        });
        displayProductInfo();

        return v;
    }

    public void displayProductInfo() {
        productPrice = bundle.getString("price");
        productName = bundle.getString("name");
        productDesc = bundle.getString("description");
        productImg = bundle.getString("img");
        productId = bundle.getString("id");
        productState = bundle.getString("state");
        changeCartBtn(productState);
        nameTxt.setText(productName);
        descTxt.setText(productDesc);
        priceTxt.setText(productPrice + "€");
        Picasso.get()
                .load(productImg)
                .into(imgView);
    }

    public void goBackToCatalog() {
        final int homeContainer = R.id.shopPage;
        CatalogPage catalogPage = new CatalogPage();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(homeContainer, catalogPage);
        ft.commit();
    }

    public void addToCart(String productId, Context context) {
        String postUrl = API.API_URL + "/carrinho/" + Preferences.readUserEmail() + "/" + productId;
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, null,
                response -> {
                    try {
                        addToCartBtn.setVisibility(View.GONE);
                        removeFromCartBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(context, R.string.addToCartResponse, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(context, "Erro: " + error, Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("language", Preferences.readLanguage());
                headers.put("Authorization", "Bearer " + Preferences.readUserToken());
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public void removeFromCart(String productId, Context context) {
        String postUrl = API.API_URL + "/carrinho/" + Preferences.readUserEmail() + "/" + productId;
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, postUrl, null,
                response -> {
                    try {
                        addToCartBtn.setVisibility(View.VISIBLE);
                        removeFromCartBtn.setVisibility(View.GONE);
                        Toast.makeText(context, R.string.removedFromCartResponse, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(context, "Erro: " + error, Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("language", Preferences.readLanguage());
                headers.put("Authorization", "Bearer " + Preferences.readUserToken());
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public void changeCartBtn(String state) {
        if (state.equals("0")) {
            addToCartBtn.setVisibility(View.VISIBLE);
            removeFromCartBtn.setVisibility(View.GONE);
        } else if (state.equals("1")) {
            addToCartBtn.setVisibility(View.GONE);
            removeFromCartBtn.setVisibility(View.VISIBLE);
        }
    }
}