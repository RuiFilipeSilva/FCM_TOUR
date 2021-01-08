package com.example.fcm_tour.Views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fcm_tour.R;

public class RoomPage extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_room_page, container, false);

        final int homeContainer = R.id.listRooms;
        Rooms rooms = new Rooms();
        openFragment(rooms, homeContainer);

        v.findViewById(R.id.qrCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rooms.getRoomsAccess(v);
            }
        });

        return v;
    }

    private void openFragment(Rooms rooms, int homeContainer) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(homeContainer, rooms);
        ft.commit();
    }
}