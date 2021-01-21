package com.example.fcm_tour.Views;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fcm_tour.API;
import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.Controllers.Users;
import com.example.fcm_tour.R;
import com.example.fcm_tour.SideBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.fcm_tour.Controllers.Users.handleError;
import static com.facebook.FacebookSdk.getApplicationContext;


public class QuizzPage extends Fragment {
    View v;
    Button startQuizzbtn, btnA, btnB, btnC, btnD, endQuizzBtn, ignoreQuizz, authBtn;
    String answer, currentAnswer;
    TextView questionTxt, resultQuizzTxt;
    int currentQuestionId, correctAnswerCount;
    String[] questions, optionA, optionB, optionC, optionD, correctAnswers;
    LinearLayout startLayout, quizzLayout, endLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferences.init(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_quizz_page, container, false);
        startLayout = v.findViewById(R.id.startLayout);
        authBtn = v.findViewById(R.id.authBtn);
        ignoreQuizz = v.findViewById(R.id.ignoreQuizz);
        startQuizzbtn = v.findViewById(R.id.startQuizzBtn);
        quizzLayout = v.findViewById(R.id.quizzLayout);
        btnA = v.findViewById(R.id.btnA);
        btnB = v.findViewById(R.id.btnB);
        btnC = v.findViewById(R.id.btnC);
        btnD = v.findViewById(R.id.btnD);
        questionTxt = v.findViewById(R.id.questionTxtView);
        endLayout = v.findViewById(R.id.endLayout);
        endQuizzBtn = v.findViewById(R.id.endQuizzBtn);
        resultQuizzTxt = v.findViewById(R.id.resultTxt);
        if (Preferences.readUserToken() != null) {
            Preferences.removeQuizzState();
            startQuizzbtn.setVisibility(View.VISIBLE);
            authBtn.setVisibility(View.GONE);
            GetQuestions(getContext());
        }
        authBtn.setOnClickListener(v -> {
            Preferences.saveQuizzState();
            Intent intent = new Intent(v.getContext(), Authentication.class);
            startActivity(intent);
        });
        startQuizzbtn.setOnClickListener(v -> {
            startQuizz();
        });
        ignoreQuizz.setOnClickListener(v -> {
            openHomeFragment();
        });
        btnA.setOnClickListener(v -> {
            answer = btnA.getTag().toString();
            checkAnswer();
        });
        btnB.setOnClickListener(v -> {
            answer = btnB.getTag().toString();
            checkAnswer();
        });
        btnC.setOnClickListener(v -> {
            answer = btnC.getTag().toString();
            checkAnswer();
        });
        btnD.setOnClickListener(v -> {
            answer = btnD.getTag().toString();
            checkAnswer();
        });
        endQuizzBtn.setOnClickListener(v -> {
            openHomeFragment();
        });
        return v;
    }

    public void startQuizz() {
        startLayout.setVisibility(View.GONE);
        quizzLayout.setVisibility(View.VISIBLE);
    }

    public void endQuizz() {
        quizzLayout.setVisibility(View.GONE);
        endLayout.setVisibility(View.VISIBLE);
        if (correctAnswerCount == 5) {
            sendEmail(getContext());
            resultQuizzTxt.setText(R.string.quizzWonPrize);
        } else {
            resultQuizzTxt.setText(R.string.quizzParticipation);
        }
    }

    public void checkAnswer() {
        if (answer.equals(currentAnswer)) {
            correctAnswerCount++;
        }
        currentQuestionId++;
        displayNextQuestion(currentQuestionId);
    }

    public void displayNextQuestion(int index) {
        if (index < 5) {
            questionTxt.setText(questions[index]);
            btnA.setText(optionA[index]);
            btnB.setText(optionB[index]);
            btnC.setText(optionC[index]);
            btnD.setText(optionD[index]);
            currentAnswer = correctAnswers[index];
        } else {
            endQuizz();
        }
    }

    public void GetQuestions(Context context) {
        String postUrl = API.API_URL + "/quizz";
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, postUrl, null,
                response -> {
                    try {
                        JSONArray jsonResponse = response.getJSONArray("questions");
                        questions = new String[jsonResponse.length()];
                        optionA = new String[jsonResponse.length()];
                        optionB = new String[jsonResponse.length()];
                        optionC = new String[jsonResponse.length()];
                        optionD = new String[jsonResponse.length()];
                        correctAnswers = new String[jsonResponse.length()];
                        currentQuestionId = 0;
                        for (int i = 0; i < jsonResponse.length(); i++) {
                            JSONObject jsonObject = jsonResponse.getJSONObject(i);
                            String jsonQuestion = jsonObject.getString("question");
                            String optionsResponse = jsonObject.getString("options");
                            JSONObject optionsJSON = new JSONObject(optionsResponse);
                            String A = optionsJSON.getString("A");
                            String B = optionsJSON.getString("B");
                            String C = optionsJSON.getString("C");
                            String D = optionsJSON.getString("D");
                            String jsonAnswer = jsonObject.getString("answer");
                            questions[i] = jsonQuestion;
                            optionA[i] = A;
                            optionB[i] = B;
                            optionC[i] = C;
                            optionD[i] = D;
                            correctAnswers[i] = jsonAnswer;
                        }
                        displayNextQuestion(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getContext(), "Erro: " + error, Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Bearer " + Preferences.readUserToken());
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public static void sendEmail(Context context) {
        String postUrl = API.API_URL + "/premio";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject postData = new JSONObject();
        try {
            postData.put("email", Preferences.readUserEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData,
                response -> {
                },
                error -> Toast.makeText(context, "Erro" + error, Toast.LENGTH_LONG).show());
        requestQueue.add(jsonObjectRequest);
    }

    public void openHomeFragment() {
        final int homeContainer = R.id.fullpage;
        History history = new History();
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.replace(homeContainer, history);
        ft.commit();
    }
}