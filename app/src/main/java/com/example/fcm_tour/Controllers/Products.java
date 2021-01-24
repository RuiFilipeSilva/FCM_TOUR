package com.example.fcm_tour.Controllers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fcm_tour.API;
import com.example.fcm_tour.R;
import com.example.fcm_tour.SideBar;
import com.example.fcm_tour.Views.CatalogPage;
import com.example.fcm_tour.Views.Language;
import com.example.fcm_tour.Views.ProductPage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class Products {
    static String TAG = "SIGA";

    public static void AddOrder(String userEmail, String productName, String adress, String zipCode, String city, String contact, String totalPrice, Array products, Context context) throws JSONException {
        String postUrl = API.API_URL + "/encomenda/" + userEmail;
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject postData = new JSONObject();
        postData.put("name", productName);
        postData.put("adress", adress);
        postData.put("zipCode", zipCode);
        postData.put("city", city);
        postData.put("contact", contact);
        postData.put("total", totalPrice);
        postData.put("products", products);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData,
                response -> {
                    try {
                        Log.d(TAG, "getProductsByUser: " + response);
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

    public static void getOrdersByUser(String userEmail, Context context) {
        String postUrl = API.API_URL + "/encomenda/utilizador/" + userEmail;
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, postUrl, null,
                response -> {
                    try {
                        Log.d(TAG, "getProductsByUser: " + response);
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

    public static void getOrdersById(String orderId, Context context) {
        String postUrl = API.API_URL + "/encomenda/numero/" + orderId;
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, postUrl, null,
                response -> {
                    try {
                        Log.d(TAG, "getProductsByUser: " + response);
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

    public static void deleteProductByUser(String userEmail, Context context) {
        String postUrl = API.API_URL + "/produtos/" + userEmail;
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, postUrl, null,
                response -> {
                    try {
                        Log.d(TAG, "getProductsByUser: " + response);
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
