package com.example.fcm_tour.Views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fcm_tour.R;

public class SculpturePage extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sculpture_page, container, false);
        final int homeContainer = R.id.listSculptures;
        Sculptures sculptures = new Sculptures();
        openFragment(sculptures, homeContainer);
        return v;
    }

    private void openFragment(Sculptures sculptures, int homeContainer) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(homeContainer, sculptures);
        ft.commit();
    }
}