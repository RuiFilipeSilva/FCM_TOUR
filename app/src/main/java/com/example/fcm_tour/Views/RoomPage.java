package com.example.fcm_tour.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.fcm_tour.API;
import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RoomPage extends Fragment {
    static View v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_room_page, container, false);
        final int homeContainer = R.id.listRooms;
        Rooms rooms = new Rooms();
        openFragment(rooms, homeContainer);
        v.findViewById(R.id.keyBoard).setOnClickListener(v1 -> AlertDialogInsertTicket());
        v.findViewById(R.id.qrCode).setOnClickListener(v12 -> {
            Intent intent = new Intent(v12.getContext(), QrScan.class);
            startActivity(intent);
        });
        return v;
    }

    private void openFragment(Rooms rooms, int homeContainer) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(homeContainer, rooms);
        ft.commit();
    }

    class TicketScan extends AsyncTask<String, String, String> {
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
                JSONObject jsonResponse = new JSONObject(result);
                String state = jsonResponse.getString("state");
                String code = jsonResponse.getString("code");
                if (state.equals("Ticket válido")) {
                    Preferences.saveRoomsAccess(code);
                    final int homeContainer = R.id.listRooms;
                    Rooms rooms = new Rooms();
                    openFragment(rooms, homeContainer);
                } else {
                    AlertDialog alertDialog2 = new AlertDialog.Builder(getContext()).create();
                    alertDialog2.setTitle(R.string.noResultsDialog);
                    alertDialog2.setMessage("Não foi encontrado nenhum bilhete com esse número");
                    alertDialog2.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            (dialog, which) -> {
                                dialog.dismiss();
                                AlertDialogInsertTicket();
                            });
                    alertDialog2.show();
                }
            } catch (JSONException e) {
                AlertDialog alertDialog2 = new AlertDialog.Builder(getContext()).create();
                alertDialog2.setTitle(R.string.noResultsDialog);
                alertDialog2.setMessage("Não foi encontrado nenhum bilhete com esse número");
                alertDialog2.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        (dialog, which) -> {
                            dialog.dismiss();
                            AlertDialogInsertTicket();
                        });
                alertDialog2.show();
                e.printStackTrace();
            }
        }
    }

    public void AlertDialogInsertTicket() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(R.string.insertTicketAlertTitle);
        alert.setMessage("Insira o código que se encontra no bilhete");
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(60, 0, 60, 0);
        final EditText input = new EditText(getContext());
        layout.addView(input, params);
        alert.setView(layout);
        alert.setPositiveButton("Ok", (dialog, whichButton) -> {
            String value = input.getText().toString();
            new TicketScan().execute(API.API_URL + "/ticket/" + value);
        });
        alert.setNegativeButton("Cancel",
                (dialog, which) -> {
                    return;
                });
        alert.show();
    }

    public static void showQrCodeScanners() {
        v.findViewById(R.id.qrCode).setVisibility(View.VISIBLE);
        v.findViewById(R.id.cardViewQr).setVisibility(View.VISIBLE);
        v.findViewById(R.id.keyBoard).setVisibility(View.VISIBLE);
        v.findViewById(R.id.cardViewKeyboard).setVisibility(View.VISIBLE);
    }
}