package com.example.fcm_tour.Views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fcm_tour.R;

public class Description extends Fragment {
    String description;
    TextView descTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_description, container, false);
        Bundle bundle = this.getArguments();
        description = bundle.getString("description");
        descTxt = v.findViewById(R.id.txt);
        descTxt.setText(description);
        return v;
    }
}