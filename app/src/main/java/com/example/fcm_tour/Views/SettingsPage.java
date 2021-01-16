package com.example.fcm_tour.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fcm_tour.API;
import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.Controllers.Users;
import com.example.fcm_tour.R;
import com.example.fcm_tour.SideBar;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class SettingsPage extends Fragment {
    View v;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_settings_page, container, false);
        loadUserInfo();

        CardView logout = v.findViewById(R.id.remove);
        logout.setOnClickListener(v -> {
            logout();
        });

        CardView delete = v.findViewById(R.id.delete);
        delete.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
            alertDialog.setTitle("ATENÇÃO");
            alertDialog.setMessage("Pretende eliminar a sua conta?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Eliminar",
                    (dialog, which) -> {
                        DeleteUser(getContext());
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar",
                    (dialog, which) -> {
                        dialog.dismiss();
                    });
            alertDialog.show();
        });

        CardView changePassword = v.findViewById(R.id.password);
        delete.setOnClickListener(v -> {

        });

        return v;
    }

    public void DeleteUser(Context context) {
        String postUrl = API.API_URL + "/delete/" + Preferences.readUserEmail();
        RequestQueue requestQueue = Volley.newRequestQueue(context);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, postUrl, null,
                response -> {
                    try {
                        logout();
                        Intent homePage = new Intent(context, SideBar.class);
                        homePage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(homePage);
                        Toast toast = Toast.makeText(context, "Conta Eliminada", Toast.LENGTH_SHORT);
                        toast.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                },
                error -> handleError(error, context)) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-HTTP-Method-Override", "DELETE");
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public static void handleError(VolleyError error, Context context) {
        String body = null;
        try {
            body = new String(error.networkResponse.data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        Toast toast = Toast.makeText(context, R.string.errorToast + body, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void loadUserInfo() {
        String userPicture = Preferences.readUserImg();
        ImageView picture = v.findViewById(R.id.profilePicture);
        Picasso.get().load(userPicture).into(picture);

        TextView username = v.findViewById(R.id.userName);
        username.setText(Preferences.readUsername());

        TextView email = v.findViewById(R.id.userEmail);
        email.setText(Preferences.readUserEmail());

    }

    public void logout() {
        SideBar.updateImg();
        Users.Logout();
        final int homeContainer = R.id.fullpage;
        History history = new History();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(homeContainer, history);
        ft.commit();
    }

}