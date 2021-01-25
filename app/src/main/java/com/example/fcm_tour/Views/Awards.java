package com.example.fcm_tour.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fcm_tour.API;
import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.R;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Awards extends Fragment {
    private static String[] names, prices, imgs, numbers;
    String name, price, img;
    Bundle extras;
    ImageButton comeback;
    TextView cup;
    View v;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferences.init(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_awards, container, false);
        cup = v.findViewById(R.id.points);
        cup.setText(Preferences.readUserPoints());
        extras = new Bundle();
        GetAwards(getContext());
        comeback = (ImageButton) v.findViewById(R.id.back);
        comeback.setOnClickListener(v -> {
            final int homeContainer = R.id.fullpage;
            Roullete roullete = new Roullete();
            backFragment(roullete, homeContainer);
        });
        return v;
    }

    private void locationSort(JSONArray result) throws JSONException {
        ListView listView = v.findViewById(R.id.listAwards);
        names = new String[result.length()];
        prices = new String[result.length()];
        imgs = new String[result.length()];
        numbers = new String[result.length()];
        for (int i = 0; i <= result.length() - 1; i++) {
            JSONObject room = result.getJSONObject(i);
            String name = room.getString("name");
            String price = room.getString("price");
            String img = room.getString("img");
            String number = room.getString("number");
            names[i] = name;
            prices[i] = price;
            imgs[i] = img;
            numbers[i] = number;
        }
        MyAdapter adapter = new MyAdapter(getContext(), names, prices, imgs, numbers);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            for (int i = 0; i < numbers.length; i++) {
                if (position == i) {
                    GetAwardsByNumber(getContext(), numbers[i]);
                    break;
                }
            }
        });
    }

    static class MyAdapter extends ArrayAdapter<String> {
        Context context;
        String aName[];
        String aPrice[];
        String aImg[];
        String aNumber[];

        MyAdapter(Context c, String name[], String price[], String img[], String number[]) {
            super(c, R.layout.award, R.id.name, name);
            this.context = c;
            this.aName = name;
            this.aPrice = price;
            this.aImg = img;
            this.aNumber = number;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.award, parent, false);
            TextView names = row.findViewById(R.id.name);
            names.setText(aName[position]);
            TextView cupertinos = row.findViewById(R.id.cupertinos);
            cupertinos.setText(aPrice[position]);
            ImageView imgs = row.findViewById(R.id.img);
            Picasso.get()
                    .load(aImg[position])
                    .into(imgs);
            return row;
        }
    }

    public void GetAwards(Context context) {
        String postUrl = API.API_URL + "/roleta/premios";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, postUrl, null,
                response -> {
                    try {
                        locationSort(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getContext(), "ERRO" + error, Toast.LENGTH_LONG)) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + Preferences.readUserToken());
                headers.put("language", Preferences.readLanguage());
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }


    public void GetAwardsByNumber(Context context, String number) {
        String postUrl = API.API_URL + "/roleta/premios/" + number;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, postUrl, null,
                response -> {
                    try {
                        name = response.getString("name");
                        price = response.getString("price");
                        img = response.getString("img");
                        extras.putString("name", name);
                        extras.putString("price", price);
                        extras.putString("img", img);
                        final int homeContainer = R.id.fullpage;
                        AwardsPage awardsPage = new AwardsPage();
                        awardsPage.setArguments(extras);
                        openFragment(awardsPage, homeContainer);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getContext(), "ERRO" + error, Toast.LENGTH_LONG)) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + Preferences.readUserToken());
                headers.put("language", Preferences.readLanguage());
                return headers;
            }

        };
        requestQueue.add(jsonObjectRequest);
    }

    private void openFragment(AwardsPage awardsPage, int homeContainer) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.from_left, R.anim.to_right);
        ft.addToBackStack(null);
        ft.replace(homeContainer, awardsPage);
        ft.commit();
    }

    private void backFragment(Roullete roullete, int homeContainer) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.from_right, R.anim.to_left);
        ft.addToBackStack(null);
        ft.replace(homeContainer, roullete);
        ft.commit();
    }
}