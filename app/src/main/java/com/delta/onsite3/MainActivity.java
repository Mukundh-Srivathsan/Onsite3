package com.delta.onsite3;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Dialog setTimeDialog;

    private TextView timeView;

    private Button setTime;

    private FloatingActionButton startStopButton;
    private FloatingActionButton pauseButton;
    private FloatingActionButton restartButton;

    private ProgressBar progressBar;

    private BroadcastReceiver broadcastReceiver;

    private int hours;
    private int minutes;
    private int seconds;
    private int time;
    private int totalTime;
    private int clickNotified;

    private boolean ispaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,
                new String[]{"FOREGROUND_SERVICE"},
                PackageManager.PERMISSION_GRANTED);

        timeView = findViewById(R.id.time);

        setTime = findViewById(R.id.setTime);

        startStopButton = findViewById(R.id.startStop);
        pauseButton = findViewById(R.id.pause);
        restartButton = findViewById(R.id.restart);

        progressBar = findViewById(R.id.progressBar);

        totalTime = getIntent().getIntExtra("totaltime", 0);
        time = getIntent().getIntExtra("time", 0);
        clickNotified = getIntent().getIntExtra("notify", 0);

        if (totalTime > 0) {
            progressBar.setMax(totalTime);
            pauseButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_pause_24));
            startStopButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_stop_24));

            Intent serviceIntent;

            switch (clickNotified) {
                case 1:
                    serviceIntent = new Intent(this, TimerService.class);
                    stopService(serviceIntent);
                    pauseButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_play_arrow_24));
                    ispaused = true;
                    break;

                case 2:
                    serviceIntent = new Intent(this, TimerService.class);
                    stopService(serviceIntent);

                    timeView.setText("00:00:00");

                    startStopButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_start_24));

                    progressBar.setProgress(0);

                    time = 0;
                    ispaused = false;
                    break;

                case 3:
                    serviceIntent = new Intent(this, TimerService.class);
                    stopService(serviceIntent);
                    serviceIntent.putExtra("time", totalTime);
                    startService(serviceIntent);

                    progressBar.setProgress(0);

                    startStopButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_stop_24));
                    pauseButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_pause_24));

                    ispaused = false;
                    break;

            }


        }

        setTimeDialog = new Dialog(this);
        setTimeDialog.setContentView(R.layout.set_time_dialog);
        setTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Countdown Timer");

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                time = intent.getIntExtra("time remaining", 0);

                int h = time / 3600;
                Log.d(TAG, "h: " + h);

                int m = (time % 3600) / 60;
                Log.d(TAG, "m: " + m);

                int s = time - (m * 60 + h * 3600);
                Log.d(TAG, "s: " + s);

                String hh, mm, ss;

                if (h < 10)
                    hh = "0" + h;
                else
                    hh = "" + h;

                if (m < 10)
                    mm = "0" + m;
                else
                    mm = "" + m;

                if (s < 10)
                    ss = "0" + s;
                else
                    ss = "" + s;

                timeView.setText(hh + ":" + mm + ":" + ss);


                progressBar.setProgress(time);
            }
        };

        registerReceiver(broadcastReceiver, intentFilter);

        setTime.setOnClickListener(v -> {

            setDialogBox();

            setTimeDialog.show();

        });

        startStopButton.setOnClickListener(v ->
        {
            Intent serviceIntent = new Intent(this, TimerService.class);
            stopService(serviceIntent);

            timeView.setText("00:00:00");

            startStopButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_start_24));

            progressBar.setProgress(0);

            time = 0;
            ispaused = false;
        });

        pauseButton.setOnClickListener(v -> {

            if (!ispaused) {
                Intent serviceIntent = new Intent(this, TimerService.class);
                stopService(serviceIntent);
                pauseButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_play_arrow_24));
                ispaused = true;
            } else {
                if (time > 0) {
                    Intent serviceIntent = new Intent(this, TimerService.class);
                    serviceIntent.putExtra("time", time);
                    startService(serviceIntent);
                    pauseButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_pause_24));
                    ispaused = false;
                }
            }

        });

        restartButton.setOnClickListener(v ->

        {
            Intent serviceIntent = new Intent(this, TimerService.class);
            stopService(serviceIntent);
            serviceIntent.putExtra("time", totalTime);
            startService(serviceIntent);

            progressBar.setProgress(0);

            startStopButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_stop_24));
            pauseButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_pause_24));

            ispaused = false;
        });
    }


    private void setDialogBox() {
        NumberPicker hourPicker = setTimeDialog.findViewById(R.id.hourpicker);
        NumberPicker minutePicker = setTimeDialog.findViewById(R.id.minutepicker);
        NumberPicker secondsPicker = setTimeDialog.findViewById(R.id.secondspicker);

        Button setTimeButton = setTimeDialog.findViewById(R.id.setbutton);
        Button cancelButton = setTimeDialog.findViewById(R.id.cancelbutton);


        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);

        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);

        secondsPicker.setMinValue(0);
        secondsPicker.setMaxValue(59);

        setTimeButton.setOnClickListener(v -> {

            Intent serviceIntent = new Intent(this, TimerService.class);

            stopService(serviceIntent);

            Log.d(TAG, "Dialog Box Service Exception");

            hours = hourPicker.getValue();
            minutes = minutePicker.getValue();
            seconds = secondsPicker.getValue();

            totalTime = hours * 3600 + minutes * 60 + seconds;

            progressBar.setMax(totalTime);

            serviceIntent.putExtra("time", totalTime);
            startService(serviceIntent);

            startStopButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_stop_24));

            ispaused = false;

            setTimeDialog.cancel();
        });

        cancelButton.setOnClickListener(v -> {
            setTimeDialog.cancel();
        });

    }
}
