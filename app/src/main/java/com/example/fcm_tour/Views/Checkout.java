package com.example.fcm_tour.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Checkout extends Fragment {
    Button comeBack, checkout;
    View v;
    EditText nameClient;
    TextView total;
    RecyclerView recyclerView;
    List<String> titles, prices;
    MyAdapter adapter;
    JSONArray shoppingCart;
    TextInputEditText name, adress, zipCode, city;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_checkout, container, false);
        recyclerView = v.findViewById(R.id.recyclerView);
        name = v.findViewById(R.id.nametxt);
        adress = v.findViewById(R.id.adressTxt);
        zipCode = v.findViewById(R.id.postalCodeTxt);
        city = v.findViewById(R.id.cityTxt);
        total = v.findViewById(R.id.total);
        total.setText(Preferences.readShoppingCartPrice().toString() + "€");
        nameClient = v.findViewById(R.id.nametxt);
        nameClient.setText(Preferences.readUsername());
        comeBack = v.findViewById(R.id.comeBack);
        comeBack.setOnClickListener(v -> returnShoppingCart());
        getShoppingCart(Preferences.readUserEmail(), getContext());
        checkout = v.findViewById(R.id.checkout);
        checkout.setOnClickListener(v -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
            alert.setTitle(R.string.conf_tittle_pay);
            alert.setMessage(getString(R.string.conf_message_pay));
            alert.setPositiveButton(R.string.confirmOrder, (dialog, whichButton) -> {
                dialog.dismiss();
                postOrder(Preferences.readUserEmail(), getContext());
            });
            alert.setNegativeButton(R.string.cancelBtn,
                    (dialog, which) -> {
                        dialog.dismiss();
                        return;
                    });

            alert.show();
        });

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

    public void locationSort(JSONArray result) throws JSONException {
        titles = new ArrayList<>();
        prices = new ArrayList<>();

        for (int i = 0; i <= result.length() - 1; i++) {
            JSONObject product = result.getJSONObject(i);
            String name = product.getString("name");
            String price = product.getString("price");
            titles.add(name);
            prices.add(price);
        }
        adapter = new MyAdapter(v.getContext(), titles, prices);
        recyclerView.setAdapter(adapter);
    }

    public void getShoppingCart(String email, Context context) {
        String postUrl = API.API_URL + "/carrinho/" + email;
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, postUrl, null,
                response -> {
                    try {
                        shoppingCart = response;
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

    public void postOrder(String email, Context context) {
        String postUrl = API.API_URL + "/encomenda/" + email;
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONArray array = new JSONArray();
        JSONObject info = new JSONObject();
        JSONObject products = new JSONObject();
        try {
            info.put("name", name.getText().toString());
            info.put("adress", adress.getText().toString());
            info.put("zipCode", zipCode.getText().toString());
            info.put("city", city.getText().toString());
            info.put("total", Preferences.readShoppingCartPrice());
            products.put("products", shoppingCart);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        array.put(info);
        array.put(products);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, postUrl, array,
                response -> {
                    try {
                        JSONObject jsonObject = response.getJSONObject(0);
                        int state = jsonObject.getInt("state");
                        if (state == 0) {
                            Toast.makeText(getContext(), R.string.toast_get_awards, Toast.LENGTH_LONG).show();
                            returnToCatalog();
                        }
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

    public void returnShoppingCart() {
        final int homeContainer = R.id.shopPage;
        ShoppingCart shoppingCart = new ShoppingCart();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.addToBackStack(null);
        ft.replace(homeContainer, shoppingCart);
        ft.commit();
    }

    public void returnToCatalog() {
        final int homeContainer = R.id.shopPage;
        CatalogPage catalogPage = new CatalogPage();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.from_right, R.anim.to_left);
        ft.replace(homeContainer, catalogPage);
        ft.commit();
    }
}