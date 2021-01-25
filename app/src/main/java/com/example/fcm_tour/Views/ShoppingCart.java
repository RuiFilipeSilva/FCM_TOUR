package com.example.fcm_tour.Views;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCart extends Fragment {
    View v;
    RecyclerView recyclerView;
    Bundle extras;
    MyAdapter adapter;
    List<String> numbers, titles, images, prices;
    TextView totalPrice;
    LinearLayout checkoutBtn, emptyCart;
    RelativeLayout cartLayout;
    int total = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferences.init(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_shopping_cart, container, false);
        extras = new Bundle();
        recyclerView = v.findViewById(R.id.recyclerView);
        emptyCart = v.findViewById(R.id.emptyCart);
        cartLayout = v.findViewById(R.id.cartLayout);
        checkoutBtn = v.findViewById(R.id.checkout);
        checkoutBtn.setOnClickListener(v -> {
            openCheckoutPage();
        });
        String email = Preferences.readUserEmail();
        getShoppingCart(email, getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        return v;
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private Context context;
        List<String> productNumbers;
        List<String> productNames;
        List<String> productImages;
        List<String> productPrices;

        public MyAdapter(Context context, List<String> productNames, List<String> productImages, List<String> productPrices, List<String> productNumbers) {
            this.context = context;
            this.productNames = productNames;
            this.productImages = productImages;
            this.productPrices = productPrices;
            this.productNumbers = productNumbers;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.product_cart, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.title.setText(productNames.get(position));
            holder.price.setText(productPrices.get(position) + "€");
            Picasso.get()
                    .load(productImages.get(position))
                    .into(holder.image);
        }

        @Override
        public int getItemCount() {
            return productNumbers.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView title;
            TextView price;
            ImageButton removeFromCartBtn;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                image = itemView.findViewById(R.id.img);
                title = itemView.findViewById(R.id.name);
                price = itemView.findViewById(R.id.prize);
                removeFromCartBtn = itemView.findViewById(R.id.remove);
                itemView.setOnClickListener(v -> {
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
                });
            }
        }

    }

    public void locationSort(JSONArray result) throws JSONException {
        numbers = new ArrayList<>();
        titles = new ArrayList<>();
        images = new ArrayList<>();
        prices = new ArrayList<>();
        total = 0;

        for (int i = 0; i <= result.length() - 1; i++) {
            JSONObject product = result.getJSONObject(i);
            String img = product.getString("img");
            String name = product.getString("name");
            String price = product.getString("price");
            String number = product.getString("number");
            images.add(img);
            titles.add(name);
            prices.add(price);
            numbers.add(number);

            total = total + Integer.parseInt(price);
        }
        Preferences.saveShoppingCartPrice(total);
        totalPrice = v.findViewById(R.id.total);
        totalPrice.setText(total + "€");
        if(total == 0) {
            cartLayout.setVisibility(View.GONE);
            emptyCart.setVisibility(View.VISIBLE);
        } else {
            cartLayout.setVisibility(View.VISIBLE);
            emptyCart.setVisibility(View.GONE);
        }
        adapter = new MyAdapter(v.getContext(), titles, images, prices, numbers);
        recyclerView.setAdapter(adapter);
    }

    public void getShoppingCart(String email, Context context) {
        String postUrl = API.API_URL + "/carrinho/" + email;
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, postUrl, null,
                response -> {
                    try {
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

    public void getProductById(String productId, Context context) {
        String postUrl = API.API_URL + "/produtos/" + productId + "/" + Preferences.readUserEmail();
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, postUrl, null,
                response -> {
                    try {
                        String id = response.getString("number");
                        String name = response.getString("name");
                        String price = response.getString("price");
                        String description = response.getString("description");
                        String img = response.getString("img");
                        String state = response.getString("state");
                        extras.putString("id", id);
                        extras.putString("name", name);
                        extras.putString("price", price);
                        extras.putString("description", description);
                        extras.putString("img", img);
                        extras.putString("state", state);
                        openProductPage();
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

    public void removeFromCart(String productId, Context context) {
        String postUrl = API.API_URL + "/carrinho/" + Preferences.readUserEmail() + "/" + productId;
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, postUrl, null,
                response -> {
                    try {
                        total = 0;
                        getShoppingCart(Preferences.readUserEmail(), context);
                        Toast.makeText(context, R.string.removedFromCartResponse, Toast.LENGTH_SHORT).show();
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

    public void openProductPage() {
        final int homeContainer = R.id.shopPage;
        ProductPage productPage = new ProductPage();
        productPage.setArguments(extras);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.addToBackStack(null);
        ft.replace(homeContainer, productPage);
        ft.commit();
    }

    public void openCheckoutPage() {
        final int homeContainer = R.id.shopPage;
        Checkout checkout = new Checkout();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.addToBackStack(null);
        ft.replace(homeContainer, checkout);
        ft.commit();
    }
}