package com.example.fcm_tour.Views;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.R;

import java.io.IOException;

public class AudioPlayer extends Fragment {
    ImageButton playBtn;
    SeekBar positionBar;
    TextView elapsedTimeLabel, remainingTimeLabel;
    static MediaPlayer mp;
    int totalTime;
    String link;
    AlertDialog dialog;
    View v;
    Integer pageType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Preferences.init(getContext());
        v = inflater.inflate(R.layout.fragment_audio_player, container, false);
        Bundle bundle = this.getArguments();
        pageType = Preferences.readPageType();
        link = bundle.getString("link");
        playBtn = v.findViewById(R.id.playBtn);
        positionBar = v.findViewById(R.id.positionBar);
        switch (pageType) {
            case 1:
                playBtn.setBackgroundResource(R.drawable.botao_play_museu);
                positionBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.museum), PorterDuff.Mode.SRC_ATOP);
                positionBar.getThumb().setColorFilter(getResources().getColor(R.color.museum), PorterDuff.Mode.SRC_ATOP);
                break;
            case 2:
                playBtn.setBackgroundResource(R.drawable.bot_o_play_biblioteca);
                positionBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.library), PorterDuff.Mode.SRC_ATOP);
                positionBar.getThumb().setColorFilter(getResources().getColor(R.color.library), PorterDuff.Mode.SRC_ATOP);
                break;
            case 3:
                playBtn.setBackgroundResource(R.drawable.bot_o_play_musica);
                positionBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.music), PorterDuff.Mode.SRC_ATOP);
                positionBar.getThumb().setColorFilter(getResources().getColor(R.color.music), PorterDuff.Mode.SRC_ATOP);
                break;
            default:
                break;
        }
        elapsedTimeLabel = v.findViewById(R.id.elapsedTimeLabel);
        remainingTimeLabel = v.findViewById(R.id.remainingTimeLabel);
        playBtn.setOnClickListener(v1 -> {
            if (!mp.isPlaying()) {
                mp.start();
                playBtn.setBackgroundResource(R.drawable.pause);

            } else {
                mp.pause();
                switch (pageType) {
                    case 0:
                        playBtn.setBackgroundResource(R.drawable.botao_play_torre);
                        break;
                    case 1:
                        playBtn.setBackgroundResource(R.drawable.botao_play_museu);
                        break;
                    case 2:
                        playBtn.setBackgroundResource(R.drawable.bot_o_play_biblioteca);
                        break;
                    case 3:
                        playBtn.setBackgroundResource(R.drawable.bot_o_play_musica);
                        break;
                    default:
                        break;
                }
            }
        });
        setProgressDialog();
        mp = new MediaPlayer();
        try {
            mp.setDataSource(link);
            mp.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.setOnPreparedListener(mp -> {
            prepareAudioLayout();
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        });

        mp.setOnCompletionListener(mp -> {
            switch (pageType) {
                case 0:
                    playBtn.setBackgroundResource(R.drawable.botao_play_torre);
                    break;
                case 1:
                    playBtn.setBackgroundResource(R.drawable.botao_play_museu);
                    break;
                case 2:
                    playBtn.setBackgroundResource(R.drawable.bot_o_play_biblioteca);
                    break;
                case 3:
                    playBtn.setBackgroundResource(R.drawable.bot_o_play_musica);
                    break;
                default:
                    break;
            }
        });
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.stop();
        mp = null;
    }

    public void prepareAudioLayout() {
        mp.seekTo(0);
        mp.setVolume(0.5f, 0.5f);
        totalTime = mp.getDuration();

        positionBar = v.findViewById(R.id.positionBar);
        positionBar.setMax(totalTime);
        positionBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            mp.seekTo(progress);
                            positionBar.setProgress(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );
        new Thread(() -> {
            while (mp != null) {
                try {
                    Message msg = new Message();
                    msg.what = mp.getCurrentPosition();
                    handler.sendMessage(msg);
                    Thread.sleep(0);
                } catch (InterruptedException ignored) {
                }
            }
        }).start();
    }

    public void setProgressDialog() {
        int llPadding = 30;
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(getContext());
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(getContext());
        tvText.setText(R.string.dialogAudioText);
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setView(ll);

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int currentPosition = msg.what;
            positionBar.setProgress(currentPosition);
            String elapsedTime = createTimeLabel(currentPosition);
            elapsedTimeLabel.setText(elapsedTime);
            String remainingTime = "- " + createTimeLabel(totalTime - currentPosition);
            remainingTimeLabel.setText(remainingTime);
            return true;
        }
    });

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;
        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;
        return timeLabel;
    }

}