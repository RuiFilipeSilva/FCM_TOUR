package com.example.fcm_tour;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.Views.Authentication;
import com.example.fcm_tour.Views.History;
import com.example.fcm_tour.Views.Museum;
import com.example.fcm_tour.Views.Tower;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class SideBar extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_bar);
        Preferences.init(getApplicationContext());
        String userPicture = Preferences.readUserImg();
        drawerLayout = findViewById(R.id.draweLayout);

        final int homeContainer = R.id.fullpage;
        History history = new History();
        openFragment(history, homeContainer);


        ImageButton picture = (ImageButton) findViewById(R.id.profilePicture);
        if (userPicture != null) {
            Picasso.get().load(userPicture).transform(new CircleTransform()).into(picture);
        }




        final DrawerLayout drawerLayout = findViewById(R.id.draweLayout);
        findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);

        setupDrawerContent(navigationView);

        //Opens Auth
        ImageButton auth = (ImageButton)navigationView.getHeaderView(0).findViewById(R.id.iconProfile);
        auth.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(view.getContext(), Authentication.class);
                startActivity(intent);
            }
        });


    }

    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());
            Log.d("SIGA", "onCreate: SEU");

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            Paint paint1 = new Paint();
            paint1.setColor(Color.BLACK);
            paint1.setStyle(Paint.Style.STROKE);
            paint1.setAntiAlias(true);
            paint1.setStrokeWidth(3);
            canvas.drawCircle(r, r, r, paint1);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
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
            default:
                fragmentClass = History.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fullpage, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        drawerLayout.closeDrawers();
    }

    private void openFragment(History history, int homeContainer) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(homeContainer, history);
        ft.commit();
    }


}