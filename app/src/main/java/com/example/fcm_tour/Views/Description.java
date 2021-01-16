package com.example.fcm_tour.Views;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fcm_tour.R;

public class Description extends Fragment {
    String description;
    TextView descTxt;
    View v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_description, container, false);
        Bundle bundle = this.getArguments();
        description = bundle.getString("description");
        descTxt = v.findViewById(R.id.txt);
        descTxt.setText(description);
        handleOnBackBtnClick();
        return v;
    }

    public void handleOnBackBtnClick() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(isVisible()) {
                    getParentFragmentManager().popBackStack();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback((LifecycleOwner) getContext(), callback);
    }
}