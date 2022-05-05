package com.brizzs.a1musicplayer.utils;

import static com.brizzs.a1musicplayer.ui.playing.PlayActivity.position;
import static com.brizzs.a1musicplayer.ui.playing.PlayActivity.songslist;
import static com.brizzs.a1musicplayer.utils.Common.artist;
import static com.brizzs.a1musicplayer.utils.Common.name;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.brizzs.a1musicplayer.R;
import com.brizzs.a1musicplayer.model.Songs;
import com.bumptech.glide.Glide;

import java.util.List;

public class Common {

    public static Uri alb_Uri = Uri.parse("content://media/external/audio/albumart");
    public static Uri art_Uri = Uri.parse("content://media/external/audio/artistart");

    public static Songs current_play = null;
    public static List<Songs> current_playlist = null;

    public static String duration = "duration";
    public static String current_list = "current_list";
    public static String current_album = "current_album";
    public static int current_position;
    public static String servicePosition = "servicePosition";
    public static String actionName = "actionName";

    public static String recently = "recently";
    public static String album = "album";
    public static String artists = "artists";
    public static String playlist = "playlist";

    public static final String MUSIC_PLAYED = "MUSIC_FILE";
    public static final String MUSIC_FILE = "MUSIC_FILE";
    public static final String MUSIC_ARTIST = "MUSIC_ARTIST";
    public static final String MUSIC_NAME = "MUSIC_NAME";
    public static final String MUSIC_IMG = "MUSIC_IMG";

    public static final String MUSIC_PLAY = "MUSIC_PLAY";
    public static final String MUSIC_PAUSE = "MUSIC_PAUSE";

    public static String value="";
    public static  String name="";
    public static  String image="";
    public static  String artist="";
    public static  String TITLE="";
    public static  String IMAGE="";
    public static  String ARTIST="";
    public static boolean SHOW_MINI_PLAYER = false;

    public static String createTime(int duration) {
        String time = "", minute="", secs = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;

        if (min<10) minute = "0"+min;
        else minute = ""+min;
        if (sec<10) secs = "0"+sec;
        else secs = ""+sec;
        time = minute+":"+secs;
        return time;
    }

    public static byte[] getImage(String uri) {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(uri);
        return metadataRetriever.getEmbeddedPicture();
    }

    public static boolean isServiceRunning(String serviceName, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if(serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public static void setView(Context context, TextView title, TextView artist, ImageView image) {

        Glide.with(context).load(songslist.get(position).getImage())
                .placeholder(R.drawable.music_note_24)
                .error(R.drawable.music_note_24)
                .into(image);

        title.setText(songslist.get(position).getName());
        artist.setText(songslist.get(position).getArtist());
    }

    public static boolean appInstalledorNot(String url, Context context) {
        PackageManager packageManager = context.getPackageManager();

        boolean app_installed;
        try {
            packageManager.getPackageInfo(url, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
            e.printStackTrace();
        }
        return app_installed;
    }
}
