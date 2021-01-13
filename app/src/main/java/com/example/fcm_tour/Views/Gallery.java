package com.example.fcm_tour.Views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.fcm_tour.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Gallery extends Fragment {
    ArrayList imgs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gallery, container, false);
        Bundle bundle = this.getArguments();
        imgs = bundle.getStringArrayList("imgsList");
        addImagesToTheGallery(v);

        return v;
    }

    private void addImagesToTheGallery(View view) {
        LinearLayout imageGallery = (LinearLayout) view.findViewById(R.id.imageGallery);
        for (int i=0; i<imgs.size(); i++) {
            imageGallery.addView(getImageView((String) imgs.get(i), i));
        }
    }

    private View getImageView(String image, int index) {
        ImageButton imageView = new ImageButton(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(50, 0, 50, 0);
        imageView.setId(index);
        imageView.setBackgroundTintMode(null);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SIGA", "onClick: " + imageView.getId());
            }
        });

        imageView.setLayoutParams(lp);
        Picasso.get()
                .load(image)
                .resize(500, 500)
                .centerCrop()
                .into(imageView);
        return imageView;
    }
}