package com.example.fcm_tour.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.fcm_tour.R;
import com.example.fcm_tour.SideBar;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

public class Shop extends AppCompatActivity {
    SpaceNavigationView navigationView;
    LinearLayout leaveShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        navigationView = findViewById(R.id.space);
        leaveShop = findViewById(R.id.leaveShop);
        leaveShop.setOnClickListener(v -> {
            Intent i = new Intent(this, SideBar.class);
            startActivity(i);
        });
        openCatalog();

        navigationView.initWithSaveInstanceState(savedInstanceState);
        navigationView.addSpaceItem(new SpaceItem("", R.drawable.painting));
        navigationView.addSpaceItem(new SpaceItem("", R.drawable.order));


        navigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                navigationView.setCentreButtonSelectable(true);
                openCart();
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                switch (itemIndex) {
                    case 0:
                        openCatalog();
                        break;
                    case 1:
                       openOrder();
                        break;
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
            }
        });


    }

    public void openCatalog() {
        final int homeContainer = R.id.shopPage;
        CatalogPage catalogPage = new CatalogPage();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(homeContainer, catalogPage);
        ft.commit();
    }

    public void openCart() {
        final int homeContainer = R.id.shopPage;
        ShoppingCart shoppingCart = new ShoppingCart();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(homeContainer, shoppingCart);
        ft.commit();
    }

    public void openOrder() {
        final int homeContainer = R.id.shopPage;
        Order order = new Order();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(homeContainer, order);
        ft.commit();
    }
}