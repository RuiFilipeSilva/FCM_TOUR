package com.example.fcm_tour.Views;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.fcm_tour.API;
import com.example.fcm_tour.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Rooms#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Rooms extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Rooms() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Rooms.
     */
    // TODO: Rename and change types and number of parameters
    public static Rooms newInstance(String param1, String param2) {
        Rooms fragment = new Rooms();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rooms, container, false);

        new GetRooms().execute(API.API_URL+"/torre/salas");

        return v;
    }


    private void locationSort(String result) {
        View v = getView();
        Log.d("SIGA", "locationSort: "+ result);
       LinearLayout roomsLayout = (LinearLayout) v.findViewById(R.id.roomMaker);
       /* for (int i = 0; i <= result.length(); i++) {

            LayoutInflater inflater = getLayoutInflater();

            Button btnTag = (Button) inflater.inflate(R.layout.buttons, null,
                    false);
            for (int j = 0; j < 4; j++) {

                btnTag.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                btnTag.setText("Button");
                btnTag.setBackgroundResource(R.drawable.alpha_button_selector);
                btnTag.setClickable(true);
                btnTag.setTextColor(Color.WHITE);
                btnTag.setGravity(Gravity.CENTER);
                btnTag.setId(j);
            }
            roomsLayout.addView(btnTag);

            btnTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "this is test", Toast.LENGTH_SHORT).show();
                }
            });
        } */
    }


    class GetRooms extends AsyncTask<String, String, String> {
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
            View v = getView();
            try {
                JSONArray jsonResponse = new JSONArray(result);
                locationSort(result);
                JSONObject jsonObjetcs = jsonResponse.getJSONObject(0);
                String TEMP = jsonObjetcs.getString("description");
                String img = jsonObjetcs.getString("cover");

                ImageView imgChuck = v.findViewById(R.id.cover);
                Picasso.get()
                        .load(img)
                        .into(imgChuck);

                TextView text = (TextView) v.findViewById(R.id.textview2);
                text.setText(TEMP);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}