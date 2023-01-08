package com.brizzs.a1musicplayer.utils;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.StrictMode;

import com.brizzs.a1musicplayer.ads.AppOpenManager;
import com.brizzs.a1musicplayer.dao.AlbumDao;
import com.brizzs.a1musicplayer.dao.ArtistDao;
import com.brizzs.a1musicplayer.dao.PlayListDao;
import com.brizzs.a1musicplayer.db.AlbumDB;
import com.brizzs.a1musicplayer.db.ArtistDB;
import com.brizzs.a1musicplayer.db.PlayListDB;

public class MyApplication extends Application {

    public static final String CHANNEL = "A1 Musics";
    public static final String DESCRIPTION = "Music Player";
    public static final String PREVIOUS = "PREVIOUS";
    public static final String NEXT = "NEXT";
    public static final String PLAY = "PLAY";
    public static final String PAUSE = "PAUSE";
    public static final String REPLAY_10 = "REPLAY_10";
    public static final String FORWARD_10 = "FORWARD_10";
    public static final String REMOVE = "REMOVE";
    public static final String NAME = "PLAYER";

    private static MyApplication instance;
    private PlayListDao playListDao;
    private AlbumDao albumDao;
    private ArtistDao artistDao;

    public AlbumDao getAlbumDao(){
        if (albumDao == null){
            AlbumDB db = AlbumDB.getInstance(getApplicationContext());
            albumDao = db.albumDao();
        }
        return albumDao;
    }

    public ArtistDao getArtistDao(){
        if (artistDao == null){
            ArtistDB db = ArtistDB.getInstance(getApplicationContext());
            artistDao = db.artistDao();
        }
        return artistDao;
    }

    public PlayListDao getPlayListDao(){
        if (playListDao == null){
            PlayListDB db = PlayListDB.getInstance(getApplicationContext());
            playListDao = db.playListDao();
        }
        return playListDao;
    }

    public static MyApplication getInstance(){
        return instance;
    }

    AppOpenManager appOpenManager;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

//        MobileAds.initialize(this, initializationStatus -> {});
//        appOpenManager = new AppOpenManager(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL, NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(DESCRIPTION);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                channel.setAllowBubbles(true);
            }
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(false);

            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


    }

}
