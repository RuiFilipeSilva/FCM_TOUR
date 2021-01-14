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

public class Rooms extends Fragment {
    private static String[] numbers, names, imgs;
    private static int[] colors, icons;
    private static View actualView;
    String description, link, title, imgsList;
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
        Preferences.removeRoomsAccess();
        new GetRooms().execute(API.API_URL + "/torre/salas");
        return actualView;
    }

    private void locationSort(JSONArray result) throws JSONException {
        View v = getView();
        ListView listView = (ListView) v.findViewById(R.id.listCards);
        numbers = new String[result.length()];
        names = new String[result.length()];
        imgs = new String[result.length()];
        colors = new int[result.length()];
        icons = new int[result.length()];
        for (int i = 0; i <= result.length() - 1; i++) {
            JSONObject room = result.getJSONObject(i);
            String img = room.getString("cover");
            String name = room.getString("name");
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
                        new GetRoomsByNumber().execute(API.API_URL + "/torre/salas/" + numbers[i]);
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
                        new GetRoomsByNumber().execute(API.API_URL + "/torre/salas/" + numbers[i]);
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
            try {
                JSONArray jsonResponse = new JSONArray(result);
                locationSort(jsonResponse);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class GetRoomsByNumber extends AsyncTask<String, String, String> {

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
                JSONObject rooms = new JSONObject(result);
                imgsList = rooms.getString("imgs");
                description = rooms.getString("description");
                link = rooms.getString("audio");
                title = rooms.getString("name");
                extras.putString("imgsList", imgsList);
                extras.putString("title", title);
                extras.putString("description", description);
                extras.putString("link", link);
                Preferences.saveAudioPageType(0);
                final int homeContainer = R.id.fullpage;
                AudioPage audioPage = new AudioPage();
                audioPage.setArguments(extras);
                openFragment(audioPage, homeContainer);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void getRoomsAccess(Context v) {
        Preferences.saveRoomsAccess();
        MyAdapter adapter = new MyAdapter(actualView.getContext(), names, numbers, imgs, colors, icons);
        ListView listView = (ListView) actualView.findViewById(R.id.listCards);
        listView.setAdapter(adapter);
    }

    private void openFragment(AudioPage audioPage, int homeContainer) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(homeContainer, audioPage);
        ft.commit();
    }

    public void AlertDialogInsertTicket() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Não têm acesso para entrar");
        alert.setMessage("Digitalize o seu bilhete para ter acesso");
        alert.setPositiveButton("Digitalize o Código", (dialog, whichButton) -> {
            dialog.dismiss();
            Intent intent = new Intent(getContext(), QrScan.class);
            startActivity(intent);
        });
        alert.setNegativeButton("Voltar",
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
}
