package com.example.fcm_tour.Views;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.fcm_tour.API;
import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Order extends Fragment {
    View v;
    List<String> numbers, titles, totals;
    RecyclerView recyclerView;
    MyAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferences.init(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_order, container, false);
        String email = Preferences.readUserEmail();
        getOrders(email, getContext());
        return v;
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private Context context;
        List<String> productNumbers;
        List<String> productNames;
        List<String> productPrices;

        public MyAdapter(Context context, List<String> productNames,  List<String> productPrices, List<String> productNumbers) {
            this.context = context;
            this.productNames = productNames;
            this.productPrices = productPrices;
            this.productNumbers = productNumbers;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.order, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.title.setText(productNames.get(position));
            holder.price.setText(productPrices.get(position) + "€");
        }

        @Override
        public int getItemCount() {
            return productNumbers.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            TextView price;
            ImageButton removeFromCartBtn;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.name);
                price = itemView.findViewById(R.id.prize);
                removeFromCartBtn = itemView.findViewById(R.id.remove);
/*                itemView.setOnClickListener(v -> {
                    for (int i = 0; i < productNumbers.size(); i++) {
                        if (getAdapterPosition() == i) {
                            getProductById(productNumbers.get(i), v.getContext());
                        }
                    }
                });

                removeFromCartBtn.setOnClickListener(v -> {
                    for (int i = 0; i < productNumbers.size(); i++) {
                        if (getAdapterPosition() == i) {
                            removeFromCart(productNumbers.get(i), getContext());
                        }
                    }
                });*/
            }
        }

    }

    public void locationSort(JSONArray result) throws JSONException {
        numbers = new ArrayList<>();
        titles = new ArrayList<>();
        totals = new ArrayList<>();

        for (int i = 0; i <= result.length() - 1; i++) {
            JSONObject product = result.getJSONObject(i);
            String name = product.getString("name");
            String total = product.getString("total");
            String number = product.getString("number");
            titles.add(name);
            totals.add(total);
            numbers.add(number);
        }
        Log.d("SIGA", "locationSort: " + titles);
        adapter = new MyAdapter(v.getContext(), titles, totals, numbers);
        recyclerView.setAdapter(adapter);
    }

    public void getOrders(String email, Context context) {
        String postUrl = API.API_URL + "/encomenda/utilizador/" + email;
        Log.d("SIGA", ": " + postUrl);
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, postUrl, null,
                response -> {
                    try {
                        Log.d("SIGA", "getOrders: " + response);
                        locationSort(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(context, "Erro: " + error, Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("language", Preferences.readLanguage());
                headers.put("Authorization", "Bearer " + Preferences.readUserToken());
                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }
}