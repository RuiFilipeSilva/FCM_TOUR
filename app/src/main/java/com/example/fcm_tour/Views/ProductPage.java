package com.example.fcm_tour.Views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fcm_tour.Controllers.Products;
import com.example.fcm_tour.R;
import com.squareup.picasso.Picasso;

public class ProductPage extends Fragment {
    Bundle bundle;
    static View v;
    static ImageView imgView;
    static TextView nameTxt, priceTxt, descTxt;
    static Button goBackBtn;
    static String productId, productName, productPrice, productDesc, productImg;

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
        displayProductInfo();

        return v;
    }

    public void displayProductInfo() {
        productPrice = bundle.getString("price");
        productName = bundle.getString("name");
        productDesc = bundle.getString("description");
        productImg = bundle.getString("img");
        productId = bundle.getString("id");
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
}