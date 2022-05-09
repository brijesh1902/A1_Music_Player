package com.brizzs.a1musicplayer.utils;

import android.Manifest;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.StrictMode;
import androidx.core.app.NotificationManagerCompat;

import com.brizzs.a1musicplayer.ads.AppOpenManager;
import com.google.android.gms.ads.MobileAds;

public class App extends Application {

    public static final String CHANNEL = "CHANNEL1";
    public static final String PREVIOUS = "PREVIOUS";
    public static final String NEXT = "NEXT";
    public static final String PLAY = "PLAY";
    public static final String PAUSE = "PAUSE";
    public static final String REPLAY_10 = "REPLAY_10";
    public static final String FORWARD_10 = "FORWARD_10";
    public static final String REMOVE = "REMOVE";
    public static final String NAME = "PLAYER";

    AppOpenManager appOpenManager;

    @Override
    public void onCreate() {
        super.onCreate();

        MobileAds.initialize(this, initializationStatus -> {});
//        appOpenManager = new AppOpenManager(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL, NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel 1");
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.enableVibration(false);

            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManager.createNotificationChannel(channel);
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


    }

}
