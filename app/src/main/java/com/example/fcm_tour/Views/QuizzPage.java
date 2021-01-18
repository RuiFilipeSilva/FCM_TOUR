package com.example.fcm_tour.Views;

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

import com.example.fcm_tour.API;
import com.example.fcm_tour.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class QuizzPage extends Fragment {
    View v;
    Button startQuizzbtn, btnA, btnB, btnC, btnD, endQuizzBtn, ignoreQuizz;
    String answer, currentAnswer;
    TextView questionTxt, resultQuizzTxt;
    int currentQuestionId, correctAnswerCount;
    String[] questions, optionA, optionB, optionC, optionD, correctAnswers;
    LinearLayout startLayout, quizzLayout, endLayout;
    String TAG = "SIGA";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_quizz_page, container, false);
        startLayout = v.findViewById(R.id.startLayout);
        ignoreQuizz  = v.findViewById(R.id.ignoreQuizz);
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
        new GetQuestions().execute(API.API_URL + "/quizz/");
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
        resultQuizzTxt.setText("RESPOSTAS CORRETAS: " + correctAnswerCount + "/n bla bla bla bla bla bla bla bla bla bla bla blabla bla bla");
    }

    public void checkAnswer() {
        if (answer.equals(currentAnswer)) {
            correctAnswerCount++;
            Log.d(TAG, "CERTO: " + correctAnswerCount);
            currentQuestionId++;
            displayNextQuestion(currentQuestionId);
        } else {
            currentQuestionId++;
            displayNextQuestion(currentQuestionId);
        }
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

    class GetQuestions extends AsyncTask<String, String, String> {
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
            }
        }
    }

    public void openHomeFragment() {
        final int homeContainer = R.id.fullpage;
        History history = new History();
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.replace(homeContainer, history);
        ft.commit();
    }
}