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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    boolean access = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_room_page, container, false);
        final int homeContainer = R.id.listRooms;
        Rooms rooms = new Rooms();
        openFragment(rooms, homeContainer);
        v.findViewById(R.id.keyBoard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogInsertTicket();
            }
        });
        v.findViewById(R.id.qrCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), QrScan.class);
                startActivity(intent);
            }
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
                if (state.equals("Ticket válido")) {
                    Rooms.getRoomsAccess(getContext());
                } else {
                    AlertDialog alertDialog2 = new AlertDialog.Builder(getContext()).create();
                    alertDialog2.setTitle("Sem Resultados");
                    alertDialog2.setMessage("Não foi encontrado nenhum bilhete com esse número");
                    alertDialog2.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    AlertDialogInsertTicket();
                                }
                            });
                    alertDialog2.show();
                }
            } catch (JSONException e) {
                AlertDialog alertDialog2 = new AlertDialog.Builder(getContext()).create();
                alertDialog2.setTitle("Sem Resultados");
                alertDialog2.setMessage("Não foi encontrado nenhum bilhete com esse número");
                alertDialog2.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                AlertDialogInsertTicket();
                            }
                        });
                alertDialog2.show();
                e.printStackTrace();
            }
        }
    }

    public void AlertDialogInsertTicket() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Código do Bilhete");
        alert.setMessage("Insira o código que se encontra no bilhete");
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(60, 0, 60, 0);
        // Set an EditText view to get ticket input
        final EditText input = new EditText(getContext());
        layout.addView(input, params);
        alert.setView(layout);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                new TicketScan().execute(API.API_URL + "/ticket/" + value);
            }
        });
        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        return;
                    }
                });
        alert.show();
    }
}