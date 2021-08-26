package com.delta.onsite3;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

public class TimerService extends Service {

    Timer timer;

    private static final String TAG = "TimerService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final int[] time = {intent.getIntExtra("time", 0)};

        int totalTime = time[0];

        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                Intent localIntent = new Intent();
                localIntent.setAction("Countdown Timer");

                time[0]--;

                updateNotification(time[0], totalTime);

                if (time[0] <= 0)
                    timer.cancel();

                localIntent.putExtra("time remaining", time[0]);
                sendBroadcast(localIntent);
            }
        }, 0, 1000);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        try {
            timer.cancel();
        } catch(Exception e){
            Log.d(TAG, "Timer Not Created");
        }

        super.onDestroy();
    }

    public void updateNotification(int time, int totalTime)
    {

        Intent notificationIntent = new Intent(this, MainActivity.class);

        notificationIntent.putExtra("totaltime", totalTime);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Intent pauseIntent = new Intent(this, MainActivity.class);

        pauseIntent.putExtra("notify", 1);
        pauseIntent.putExtra("totaltime", totalTime);
        pauseIntent.putExtra("time", time);

        PendingIntent pause = PendingIntent.getActivity(this,
                1,
                pauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Intent stopIntent = new Intent(this, MainActivity.class);

        stopIntent.putExtra("notify", 2);
        stopIntent.putExtra("totaltime", totalTime);
        stopIntent.putExtra("time", time);

        PendingIntent stop = PendingIntent.getActivity(this,
                2,
                stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Intent resetIntent = new Intent(this, MainActivity.class);

        resetIntent.putExtra("notify", 3);
        resetIntent.putExtra("totaltime", totalTime);
        resetIntent.putExtra("time", time);

        PendingIntent reset = PendingIntent.getActivity(this,
                3,
                resetIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

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


        Notification notification = new NotificationCompat.Builder( this, "Timer")
                .setContentTitle("Timer")
                .setContentText(hh + ":" + mm + ":" + ss)
                .setSmallIcon(R.drawable.ic_baseline_stop_24)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.ic_baseline_pause_24, "Pause", pause)
                .addAction(R.drawable.ic_baseline_stop_24, "Stop", stop)
                .addAction(R.drawable.ic_baseline_replay_24, "Reset", reset)
                .setColor(Color.argb(1,57, 255, 20))
                .build();

        startForeground(1, notification);

        NotificationChannel notificationChannel = new NotificationChannel("Timer",
                "Timer",
                NotificationManager.IMPORTANCE_LOW);

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(notificationChannel);

    }
}
