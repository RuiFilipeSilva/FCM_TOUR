package com.example.fcm_tour.Controllers;

import android.content.Context;
import android.widget.Toast;

public class ToastMaker {
    private static Context mContext;

    public ToastMaker(Context context) {
        mContext = context;
    }

    //Do a toast notification
    public static void sampleToast(String txt)
    {

        Toast toast = Toast.makeText(mContext, txt, Toast.LENGTH_SHORT);

        toast.show();
    }

}
