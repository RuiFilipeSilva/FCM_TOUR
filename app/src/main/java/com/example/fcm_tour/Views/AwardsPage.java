package com.example.fcm_tour.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.example.fcm_tour.R;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class AwardsPage extends Fragment {
    Bundle extras, bundle;
    String name, price, imgs, actualPoints, email;
    ImageButton comeback;
    EditText nameClient, adress, postalCode, city;
    LinearLayout form;
    Button getAward;
    RelativeLayout alert;
    Integer points, cupertinos;
    TextView cup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = this.getArguments();
        extras = new Bundle();
        Preferences.init(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_awards_page, container, false);
        email = Preferences.readUserEmail();
        actualPoints = Preferences.readUserPoints();
        cup = v.findViewById(R.id.points);
        cup.setText(actualPoints);
        points = Integer.parseInt(actualPoints);
        name = bundle.getString("name");
        TextView text = (TextView) v.findViewById(R.id.name);
        text.setText(name);
        price = bundle.getString("price");
        TextView prices = (TextView) v.findViewById(R.id.cupertinos);
        prices.setText(price);
        cupertinos = Integer.parseInt(price);
        imgs = bundle.getString("img");
        ImageView img = (ImageView) v.findViewById(R.id.img);
        Picasso.get().load(imgs).into(img);
        comeback = (ImageButton) v.findViewById(R.id.back);

        form = v.findViewById(R.id.form);
        alert = v.findViewById(R.id.alert);
        nameClient = v.findViewById(R.id.nametxt);
        adress = v.findViewById(R.id.adressTxt);
        postalCode = v.findViewById(R.id.postalCodeTxt);
        city = v.findViewById(R.id.cityTxt);
        getAward = v.findViewById(R.id.getAward);
        nameClient.setText(Preferences.readUsername());
        comeback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int homeContainer = R.id.fullpage;
                Awards awards = new Awards();
                backFragment(awards, homeContainer);
            }
        });

        if (cupertinos <= points) {
            form.setVisibility(View.VISIBLE);
            alert.setVisibility(View.INVISIBLE);
            getAward.setText(R.string.get_awards);
            getAward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("SIGA", "onClick: AQUI");
                    if (nameClient.getText().toString().equals("") || adress.getText().toString().equals("") || postalCode.getText().toString().equals("") || city.getText().toString().equals("")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme).create();
                        alertDialog.setTitle(R.string.title_get_awards);
                        alertDialog.setMessage(getString(R.string.message_get_award));
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                (dialog, which) -> {
                                    dialog.dismiss();
                                });
                        alertDialog.show();
                    } else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
                        alert.setTitle(R.string.conf_title_get_awards);
                        alert.setMessage(getString(R.string.conf_message_get_awards));
                        alert.setPositiveButton("Confirmar", (dialog, whichButton) -> {
                            points = points - cupertinos;
                            actualPoints = String.valueOf(points);
                            Preferences.write("userPoints", actualPoints);
                            cup.setText(actualPoints);
                            updatePoints(email, actualPoints, getContext());
                            dialog.dismiss();
                            final int homeContainer = R.id.fullpage;
                            Awards awards = new Awards();
                            backFragment(awards, homeContainer);
                            Toast.makeText(getContext(), R.string.toast_get_awards, Toast.LENGTH_LONG).show();
                        });
                        alert.setNegativeButton("Cancelar",
                                (dialog, which) -> {
                                    dialog.dismiss();
                                    return;
                                });

                        alert.show();
                    }

                }
            });
        } else {
            form.setVisibility(View.INVISIBLE);
            getAward.setText(R.string.back);
            alert.setVisibility(View.VISIBLE);
            getAward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int homeContainer = R.id.fullpage;
                    Awards awards = new Awards();
                    backFragment(awards, homeContainer);

                }
            });

        }


        return v;
    }

    private void backFragment(Awards awards, int homeContainer) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.from_right, R.anim.to_left);
        ft.addToBackStack(null);
        ft.replace(homeContainer, awards);
        ft.commit();
    }

    public static void updatePoints(String email, String newPoints, Context context) {
        String postUrl = API.API_URL + "/points/" + email;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject postData = new JSONObject();
        try {
            postData.put("points", newPoints);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, postUrl, postData,
                response -> {
                    try {
                        Preferences.write("userPoints", newPoints);
                    } catch (Exception e) {
                        Log.d("SIGA", "changeDate: " + e);
                        e.printStackTrace();
                    }
                },
                error -> handleError(error, context)) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                Log.d("SIGA", "getHeaders: Aqui");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
    public static void handleError(VolleyError error, Context context) {
        Log.d("SIGA", "handleError: " + error);
        String body = null;
        try {
            body = new String(error.networkResponse.data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        Toast toast = Toast.makeText(context, R.string.errorToast + body, Toast.LENGTH_SHORT);
        toast.show();
    }
}