package com.example.fcm_tour.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.fcm_tour.API;
import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.R;
import com.example.fcm_tour.SideBar;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PaintingQrCodes extends AppCompatActivity {
    public CodeScanner mCodeScanner;
    public static final int MY_CAMERA_REQUEST_CODE = 100;
    String numberResult;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferences.init(getApplicationContext());
        setContentView(R.layout.activity_painting_qr_codes);
        extras = new Bundle();
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            numberResult = result.getText();
            TicketScan(numberResult);
        }));
        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        } else {
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
                Toast.makeText(this, R.string.permissionGranted, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.permissionDenied, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void TicketScan(String number) {
        String postUrl = API.API_URL + "/museu/quadros/" + number;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, postUrl, null,
                response -> {
                    try {
                        String state = response.getString("number");
                        if (state.equals(numberResult)) {
                            AlertDialog alertDialog = new AlertDialog.Builder(PaintingQrCodes.this, R.style.MyDialogTheme).create();
                            alertDialog.setTitle(R.string.validResultDialog);
                            alertDialog.setMessage(getString(R.string.validMessageDialog));
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    (dialog, which) -> {
                                        Preferences.write("qrPaint", numberResult);
                                        dialog.dismiss();
                                        Intent i = new Intent(getApplicationContext(), SideBar.class);
                                        startActivity(i);
                                    });
                            alertDialog.show();
                        } else {
                            AlertDialog alertDialog = new AlertDialog.Builder(PaintingQrCodes.this, R.style.MyDialogTheme).create();
                            alertDialog.setTitle(R.string.noResultsDialog);
                            alertDialog.setMessage(getString(R.string.invalidMessageCode));
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    (dialog, which) -> {
                                        dialog.dismiss();
                                        mCodeScanner.startPreview();
                                    });
                            alertDialog.show();
                        }
                    } catch (JSONException e) {
                        AlertDialog alertDialog2 = new AlertDialog.Builder(PaintingQrCodes.this, R.style.MyDialogTheme).create();
                        alertDialog2.setTitle(R.string.noResultsDialog);
                        alertDialog2.setMessage(getString(R.string.invalidMessageTicket));
                        alertDialog2.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                (dialog, which) -> {
                                    dialog.dismiss();
                                    mCodeScanner.startPreview();
                                });
                        alertDialog2.show();
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getApplicationContext(), "Erro: " + error, Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("language", Preferences.readLanguage());
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}