package com.example.fcm_tour.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Rooms extends Fragment {
    private static String[] numbers, names, imgs;
    private static int[] colors, icons;
    private static View actualView;
    String description, link, title, imgsList, currentTicket;
    Bundle extras;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferences.init(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        actualView = inflater.inflate(R.layout.fragment_rooms, container, false);
        extras = new Bundle();
        currentTicket = Preferences.readRoomsAccessCode();
        loadRoomsLayout();
        return actualView;
    }

    private void locationSort(JSONArray result) throws JSONException {
        View v = getView();
        ListView listView = v.findViewById(R.id.listCards);
        numbers = new String[result.length()];
        names = new String[result.length()];
        imgs = new String[result.length()];
        colors = new int[result.length()];
        icons = new int[result.length()];
        for (int i = 0; i <= result.length() - 1; i++) {
            JSONObject room = result.getJSONObject(i);
            String img = room.getString("cover");
            String name;
            if (i == 0 && Preferences.readLanguage().equals("EN")) {
                name = "Beginning";
            } else {
                name = room.getString("name");
            }
            //String name = room.getString("name");
            String number = room.getString("number");
            if (i == 0) {
                int color = R.color.tower;
                colors[i] = color;
                int icon = R.drawable.ic_baseline_play_arrow_24;
                icons[i] = icon;
            } else {
                int color = R.color.black;
                colors[i] = color;
                int icon = R.drawable.ic_baseline_lock_;
                icons[i] = icon;
            }
            names[i] = name;
            numbers[i] = number;
            imgs[i] = img;
        }
        MyAdapter adapter = new MyAdapter(getContext(), names, numbers, imgs, colors, icons);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            boolean access = true;
            for (int i = 0; i < numbers.length; i++) {
                if (Preferences.readRoomsAccess() == false) {
                    if (position == i && position == 0) {
                        access = true;
                        Preferences.removeRoom();
                        Preferences.write("room", numbers[i]);
                        extras.putString("nextRoom", names[i + 1]);
                        GetRoomsByNumber(getContext(), numbers[i]);
                        break;
                    } else {
                        access = false;
                    }
                } else {
                    if (position == i) {
                        Preferences.removeRoom();
                        Preferences.write("room", numbers[i]);
                        if (i < names.length - 1) {
                            extras.putString("nextRoom", names[i + 1]);
                        }
                        GetRoomsByNumber(getContext(), numbers[i]);
                    }
                }
            }
            if (Preferences.readRoomsAccess() == false && access == false) {
                AlertDialogInsertTicket();
            }
        });
    }

    static class MyAdapter extends ArrayAdapter<String> {
        Context context;
        String rName[];
        String rNumber[];
        String rImg[];
        int rColor[];
        int rIcon[];

        MyAdapter(Context c, String name[], String number[], String img[], int color[], int icon[]) {
            super(c, R.layout.room, R.id.name, name);
            this.context = c;
            this.rName = name;
            this.rNumber = number;
            this.rImg = img;
            this.rColor = color;
            this.rIcon = icon;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.room, parent, false);
            if (Preferences.readRoomsAccess() == false) {
                TextView names = row.findViewById(R.id.name);
                TextView numbers = row.findViewById(R.id.number);
                ImageView imgs = row.findViewById(R.id.backImage);
                ImageButton icons = row.findViewById(R.id.play);
                icons.setBackgroundResource(rIcon[position]);
                names.setText(rName[position]);
                numbers.setBackgroundColor(row.getResources().getColor(rColor[position]));
                numbers.setText(rNumber[position]);
                Picasso.get()
                        .load(rImg[position])
                        .into(imgs);
            } else {
                TextView names = row.findViewById(R.id.name);
                TextView numbers = row.findViewById(R.id.number);
                ImageView imgs = row.findViewById(R.id.backImage);
                ImageButton icons = row.findViewById(R.id.play);
                icons.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                names.setText(rName[position]);
                numbers.setBackgroundColor(row.getResources().getColor(R.color.tower));
                numbers.setText(rNumber[position]);
                Picasso.get()
                        .load(rImg[position])
                        .into(imgs);
            }
            return row;
        }
    }

    public void GetRoomsByNumber(Context context, String number) {
        String postUrl = API.API_URL + "/torre/salas/" + number;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, postUrl, null,
                response -> {
                    try {
                        imgsList = response.getString("imgs");
                        description = response.getString("description");
                        link = response.getString("audio");
                        title = response.getString("name");
                        if(title.equals("Início") && Preferences.readLanguage().equals("EN")) {
                            title = "Beginning";
                        }
                        extras.putString("imgsList", imgsList);
                        extras.putString("title", title);
                        extras.putString("description", description);
                        extras.putString("link", link);
                        Preferences.saveAudioPageType(0);
                        final int homeContainer = R.id.fullpage;
                        AudioPage audioPage = new AudioPage();
                        audioPage.setArguments(extras);
                        openFragment(audioPage, homeContainer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(context, "Erro" + error, Toast.LENGTH_LONG).show()) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("language", Preferences.readLanguage());
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
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

    public static void getRoomsAccess(String code) {
        Preferences.saveRoomsAccess(code);
        MyAdapter adapter = new MyAdapter(actualView.getContext(), names, numbers, imgs, colors, icons);
        ListView listView = actualView.findViewById(R.id.listCards);
        listView.setAdapter(adapter);
    }

    public void loadRoomsLayout() {
        new GetRooms().execute(API.API_URL + "/torre/salas");
        new ValidateTicket().execute(API.API_URL + "/ticket/" + currentTicket);
    }

    private void openFragment(AudioPage audioPage, int homeContainer) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.from_left, R.anim.to_right);
        ft.addToBackStack(null);
        ft.replace(homeContainer, audioPage);
        ft.commit();
    }

    public void AlertDialogInsertTicket() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
        alert.setTitle(R.string.noAccessAlertTitle);
        alert.setMessage(R.string.scanTicketAccessTxt);
        alert.setPositiveButton(R.string.scanCodeTxt, (dialog, whichButton) -> {
            dialog.dismiss();
            Intent intent = new Intent(getContext(), QrScan.class);
            startActivity(intent);
        });
        alert.setNegativeButton(R.string.back,
                (dialog, which) -> {
                    dialog.dismiss();
                    return;
                });

        alert.show();
    }

    public static ArrayList<String> getBeforeAfterRooms(String currentRoom) {
        ArrayList<String> result = new ArrayList<String>();
        String before = null;
        int beforeNum = 0;
        String after = null;
        int afterNum = 0;
        for (int i = 0; i < names.length; i++) {
            if (currentRoom.equals(names[i])) {
                if (i > 0) {
                    before = names[i - 1];
                    beforeNum = i - 1;
                } else {
                    before = null;
                    beforeNum = -1;
                }
                if (i < names.length - 1) {
                    after = names[i + 1];
                    afterNum = i + 1;
                } else {
                    after = null;
                    afterNum = -1;
                }
            }
        }
        result.add(before);
        result.add(after);
        result.add(String.valueOf(beforeNum));
        result.add(String.valueOf(afterNum));
        return result;
    }

    class ValidateTicket extends AsyncTask<String, String, String> {
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
                Toast.makeText(getContext(), "Exception: " + e, Toast.LENGTH_SHORT).show();
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
                    MyAdapter adapter = new MyAdapter(actualView.getContext(), names, numbers, imgs, colors, icons);
                    ListView listView = actualView.findViewById(R.id.listCards);
                    listView.setAdapter(adapter);
                } else {
                    Preferences.removeRoomsAccess();
                    MyAdapter adapter = new MyAdapter(actualView.getContext(), names, numbers, imgs, colors, icons);
                    ListView listView = actualView.findViewById(R.id.listCards);
                    listView.setAdapter(adapter);
                    RoomPage.showQrCodeScanners();
                }
            } catch (JSONException e) {
                Preferences.removeRoomsAccess();
                MyAdapter adapter = new MyAdapter(actualView.getContext(), names, numbers, imgs, colors, icons);
                ListView listView = actualView.findViewById(R.id.listCards);
                listView.setAdapter(adapter);
                RoomPage.showQrCodeScanners();
            }
        }
    }
}
