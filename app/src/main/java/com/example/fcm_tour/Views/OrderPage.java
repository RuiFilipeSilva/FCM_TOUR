package com.example.fcm_tour.Views;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderPage extends Fragment {
    View v;
    TextView stateTxt, totalTxt, orderNum;
    RecyclerView recyclerView;
    List<String> titles, prices;
    MyAdapter adapter;
    Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_order_page, container, false);
        bundle = getArguments();
        String orderId = bundle.getString("orderId");
        getOrderById(orderId, getContext());
        stateTxt = v.findViewById(R.id.orderState);
        totalTxt = v.findViewById(R.id.orderTotal);
        orderNum = v.findViewById(R.id.orderNumber);
        recyclerView = v.findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        return v;
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private Context context;
        List<String> productNames;
        List<String> productPrices;

        public MyAdapter(Context context, List<String> productNames, List<String> productPrices) {
            this.context = context;
            this.productNames = productNames;
            this.productPrices = productPrices;
        }

        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.checkout_product, parent, false);
            return new MyAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
            holder.title.setText(productNames.get(position));
            holder.price.setText(productPrices.get(position) + "€");
        }

        @Override
        public int getItemCount() {
            return productNames.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            TextView price;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.name);
                price = itemView.findViewById(R.id.price);
            }
        }
    }

    public void locationSort(JSONArray result, int state, String totalPrice, String orderNumber) throws JSONException {
        titles = new ArrayList<>();
        prices = new ArrayList<>();

        for (int i = 0; i <= result.length() - 1; i++) {
            JSONObject product = result.getJSONObject(i);
            String name = product.getString("name");
            String price = product.getString("price");
            titles.add(name);
            prices.add(price);
        }

        switch (state) {
            case 0:
                stateTxt.setText(R.string.orderState);
                break;
            case 1:
                stateTxt.setText(R.string.orderStateDone);
                break;
            default:
                break;
        }
        totalTxt.setText(totalPrice + "€");
        orderNum.setText(getString(R.string.orderTitle) + orderNumber);
        adapter = new MyAdapter(v.getContext(), titles, prices);
        recyclerView.setAdapter(adapter);
    }

    public void getOrderById(String orderId, Context context) {
        String postUrl = API.API_URL + "/encomenda/numero/" + orderId;
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, postUrl, null,
                response -> {
                    try {
                        String products = response.getString("products");
                        JSONArray productsArray = new JSONArray(products);
                        int state = response.getInt("state");
                        String totalPrice = response.getString("total");
                        locationSort(productsArray, state, totalPrice, orderId);
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
        requestQueue.add(jsonObjectRequest);
    }
}