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

import com.example.fcm_tour.API;
import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Awards extends Fragment {
    private static String[] names, prices, imgs, numbers;
    String name, price, img;
    Bundle extras;
    ImageButton comeback;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_awards, container, false);
        extras = new Bundle();
        new GetAwards().execute(API.API_URL + "/roleta/premios");
        comeback = (ImageButton)v.findViewById(R.id.back);
        comeback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int homeContainer = R.id.fullpage;
                Roullete roullete = new Roullete();
                backFragment(roullete, homeContainer);
            }
        });

        return v;

    }

    private void locationSort(JSONArray result) throws JSONException {
        View v = getView();
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
        Log.d("SIGA", "locationSort: "+ numbers);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            for (int i = 0; i < numbers.length; i++) {
                if (position == i) {
                    new GetAwardsByNumber().execute(API.API_URL + "/roleta/premios/" + numbers[i]);
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

    class GetAwards extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... fileUrl) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                URL url = new URL(fileUrl[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                stringBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (Exception e) {
                Log.e("MY_CUSTOM_ERRORS", "onCreate: " + e);
            }
            return stringBuilder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONArray jsonResponse = new JSONArray(result);
                locationSort(jsonResponse);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    class GetAwardsByNumber extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... fileUrl) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                URL url = new URL(fileUrl[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                stringBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (Exception e) {
            }
            return stringBuilder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject awards = new JSONObject(result);
                name = awards.getString("name");
                price = awards.getString("price");
                img = awards.getString("img");
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
        }
        private void openFragment(AwardsPage awardsPage, int homeContainer) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.setCustomAnimations(R.anim.from_left, R.anim.to_right);
            ft.addToBackStack(null);
            ft.replace(homeContainer, awardsPage);
            ft.commit();
        }


    }
    private void backFragment(Roullete roullete, int homeContainer) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.from_right, R.anim.to_left);
        ft.addToBackStack(null);
        ft.replace(homeContainer, roullete);
        ft.commit();
    }
}