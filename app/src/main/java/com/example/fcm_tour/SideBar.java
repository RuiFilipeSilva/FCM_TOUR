package com.example.fcm_tour;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.Controllers.Users;
import com.example.fcm_tour.Views.AudioPage;
import com.example.fcm_tour.Views.Authentication;
import com.example.fcm_tour.Views.History;
import com.example.fcm_tour.Views.Library;
import com.example.fcm_tour.Views.Museum;
import com.example.fcm_tour.Views.Music;
import com.example.fcm_tour.Views.Roullete;
import com.example.fcm_tour.Views.SettingsPage;
import com.example.fcm_tour.Views.Tower;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import de.hdodenhof.circleimageview.CircleImageView;

public class SideBar extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    static NavigationView navigationView;
    static CircleImageView picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_bar);
        Preferences.init(getApplicationContext());
        loadUserPicture();
        drawerLayout = findViewById(R.id.draweLayout);
        final int homeContainer = R.id.fullpage;
        History history = new History();
        openFragment(history, homeContainer);
        final DrawerLayout drawerLayout = findViewById(R.id.draweLayout);
        findViewById(R.id.menu).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        if (Preferences.readUserToken() == null) {
            Menu menu = navigationView.getMenu();
            MenuItem nav_dashboard = menu.findItem(R.id.logout);
            nav_dashboard.setVisible(false);
            MenuItem nav_settings= menu.findItem(R.id.def);
            nav_settings.setTitle("Alterar Idioma");

        }
        setupDrawerContent(navigationView);
        CircleImageView auth = navigationView.getHeaderView(0).findViewById(R.id.profilePicture);
        auth.setOnClickListener(view -> {
            if(Preferences.readUserToken() == null){
                Intent intent = new Intent(view.getContext(), Authentication.class);
                startActivity(intent);
            }
            else{
               openSettings();
            }

        });
    }

    public void openSettings() {
        final int homeContainer = R.id.fullpage;
        SettingsPage settingsPage = new SettingsPage();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.replace(homeContainer, settingsPage);
        ft.commit();
    }

    public void loadUserPicture() {
        String userPicture = Preferences.readUserImg();
        navigationView = findViewById(R.id.navigationView);
        CircleImageView auth = navigationView.getHeaderView(0).findViewById(R.id.profilePicture);
        picture = findViewById(R.id.profilePicture);
        if (userPicture != null && !userPicture.equals("")) {
            Picasso.get().load(userPicture).into(picture);
            Picasso.get().load(userPicture).into(auth);
        } else {
            auth.setImageResource(R.drawable.ic_launcher_background);
            picture.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.inicio:
                fragmentClass = History.class;
                break;
            case R.id.item1:
                fragmentClass = Tower.class;
                break;
            case R.id.item2:
                fragmentClass = Museum.class;
                break;
            case R.id.item3:
                fragmentClass = Library.class;
                break;
            case R.id.item4:
                fragmentClass = Music.class;
                break;
            case R.id.roleta:
                fragmentClass = Roullete.class;
                break;
            case R.id.logout:
                Users.Logout();
                loadUserPicture();
                menuItem.setVisible(false);
                fragmentClass = History.class;
                break;
            case R.id.def:
                if(Preferences.readUserToken() == null){
                    fragmentClass = History.class;

                }else {
                    fragmentClass = SettingsPage.class;
                }
                break;
            default:
                fragmentClass = History.class;
                break;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().addToBackStack(null);
        fragmentManager.beginTransaction().replace(R.id.fullpage, fragment).commit();
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        drawerLayout.closeDrawers();
    }

    private void openFragment(History history, int homeContainer) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(homeContainer, history);
        ft.commit();
    }

    public static void updateImg(){
        if(Preferences.readUserImg() == null){
            CircleImageView auth = navigationView.getHeaderView(0).findViewById(R.id.profilePicture);
            auth.setImageResource(R.drawable.ic_launcher_background);
            picture.setImageResource(R.drawable.ic_launcher_background);
        }else{
            CircleImageView auth = navigationView.getHeaderView(0).findViewById(R.id.profilePicture);
            Picasso.get().load(Preferences.readUserImg()).into(picture);
            Picasso.get().load(Preferences.readUserImg()).into(auth);
        }
    }
}