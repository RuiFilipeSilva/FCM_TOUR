package com.example.fcm_tour.Views;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fcm_tour.API;
import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.R;
import com.google.android.material.navigation.NavigationView;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatalogPage extends Fragment {
    View v;
    RecyclerView recyclerView;
    List<String> numbers, titles, images, prices;
    MyAdapter adapter;
    Bundle extras;
    Spinner spinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_catalog_page, container, false);
        recyclerView = v.findViewById(R.id.recyclerView);
        extras = new Bundle();
        spinner = v.findViewById(R.id.spinner);
        loadFilters();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        productsByLowerPrice(getContext());
                        break;
                    case 1:
                        productsByHigherPrice(getContext());
                        break;
                    case 2:
                        getProducts(getContext());
                        loadFilters();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        getProducts(getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        return v;
    }

    public void loadFilters() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount()));
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1;
            }
        };
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (Preferences.readLanguage().equals("EN")) {
            spinnerAdapter.add("Price: Ascending");
            spinnerAdapter.add("Price: Descending");
            spinnerAdapter.add("Clear");
            spinnerAdapter.add("Order By:");
        } else if (Preferences.readLanguage().equals("PT")) {
            spinnerAdapter.add("Preço: Ascendente");
            spinnerAdapter.add("Preço: Descendente");
            spinnerAdapter.add("Limpar");
            spinnerAdapter.add("Ordenar por:");
        }
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(spinnerAdapter.getCount());
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
            View view = LayoutInflater.from(context).inflate(R.layout.product, parent, false);
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

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                image = itemView.findViewById(R.id.productImg);
                title = itemView.findViewById(R.id.productName);
                price = itemView.findViewById(R.id.productPrice);

                itemView.setOnClickListener(v -> {
                    for (int i = 0; i < productNumbers.size(); i++) {
                        if (getAdapterPosition() == i) {
                            getProductById(productNumbers.get(i), v.getContext());
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
        }
        adapter = new MyAdapter(v.getContext(), titles, images, prices, numbers);
        recyclerView.setAdapter(adapter);
    }

    public void getProducts(Context context) {
        String postUrl = API.API_URL + "/produtos";
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

    public void productsByHigherPrice(Context context) {
        String postUrl = API.API_URL + "/produtos/preco/alto";
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, postUrl, null,
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
                headers.put("Authorization", "Bearer " + Preferences.readUserToken());
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public void productsByLowerPrice(Context context) {
        String postUrl = API.API_URL + "/produtos/preco/baixo";
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, postUrl, null,
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
                headers.put("Authorization", "Bearer " + Preferences.readUserToken());
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}