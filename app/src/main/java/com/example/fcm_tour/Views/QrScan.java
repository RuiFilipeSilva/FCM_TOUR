package com.example.fcm_tour.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.fcm_tour.API;
import com.example.fcm_tour.MainActivity;
import com.example.fcm_tour.R;
import com.google.zxing.Result;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class QrScan extends AppCompatActivity {
    public CodeScanner mCodeScanner;
    public static final int MY_CAMERA_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QrScan.this, result.getText(), Toast.LENGTH_SHORT).show();
                        new TicketScan().execute(API.API_URL+"/ticket/"+result.getText());
                        Log.d("SIGA", "run: " + API.API_URL+"/ticket/"+result.getText());
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
        else{
            mCodeScanner.startPreview();
        }

    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mCodeScanner.startPreview();
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();


            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    class TicketScan extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... fileUrl){
            StringBuilder stringBuilder = new StringBuilder();
            try {
                URL url = new URL(fileUrl[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                stringBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) !=null){
                    stringBuilder.append(line);
                }
            }catch (Exception e){
                Log.e("MY_CUSTOM_ERRORS", "onCreate: " + e);
            }
            return stringBuilder.toString();
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            Log.d("SIGA", "qulaquermerda: " + result);
            try {

                JSONObject jsonResponse = new JSONObject(result);
                String TEMP = jsonResponse.getString("state");
                /* JSONObject jsonObjetcs = jsonResponse.getJSONObject(0);

                String img = jsonObjetcs.getString("cover");*/
                Log.d("SIGA", "onPostExecute: " + TEMP);


            }catch (JSONException e){
                Log.d("ERRO", "onPostExecute: " + e);
                e.printStackTrace();
            }
        }
        public void openFragment(SocialMediaAuth socialMediaAuth, int container) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(container, socialMediaAuth);
            ft.commit();
        }
    }
}