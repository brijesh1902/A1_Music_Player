package com.brizzs.a1musicplayer.service;

import static androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC;
import static com.brizzs.a1musicplayer.utils.App.CHANNEL;
import static com.brizzs.a1musicplayer.utils.App.NEXT;
import static com.brizzs.a1musicplayer.utils.App.PAUSE;
import static com.brizzs.a1musicplayer.utils.App.PLAY;
import static com.brizzs.a1musicplayer.utils.App.PREVIOUS;
import static com.brizzs.a1musicplayer.utils.App.REMOVE;
import static com.brizzs.a1musicplayer.ui.playing.PlayActivity.songslist;
import static com.brizzs.a1musicplayer.utils.Common.ARTIST;
import static com.brizzs.a1musicplayer.utils.Common.IMAGE;
import static com.brizzs.a1musicplayer.utils.Common.MUSIC_ARTIST;
import static com.brizzs.a1musicplayer.utils.Common.MUSIC_FILE;
import static com.brizzs.a1musicplayer.utils.Common.MUSIC_IMG;
import static com.brizzs.a1musicplayer.utils.Common.MUSIC_NAME;
import static com.brizzs.a1musicplayer.utils.Common.MUSIC_PLAYED;
import static com.brizzs.a1musicplayer.utils.Common.TITLE;
import static com.brizzs.a1musicplayer.utils.Common.actionName;
import static com.brizzs.a1musicplayer.utils.Common.current_list;
import static com.brizzs.a1musicplayer.utils.Common.duration;
import static com.brizzs.a1musicplayer.utils.Common.servicePosition;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.media.session.MediaButtonReceiver;

import com.brizzs.a1musicplayer.model.Songs;
import com.brizzs.a1musicplayer.R;
import com.brizzs.a1musicplayer.ui.playing.PlayActivity;
import com.brizzs.a1musicplayer.utils.TinyDB;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;

public class MusicService extends Service {

    IBinder binder = new MyBinder();
    MediaPlayer mediaPlayer;
    Uri uri;
    public int position = -1;
    ActionPlaying actionPlaying;
    MediaSessionCompat mediaSessionCompat;
    Notification notification = null;
    TinyDB tinyDB;
    SharedPreferences.Editor editor;
    PendingIntent prevPending, playPending, nextPending, removePending, replayPending, forwardPending, pendingIntent;
    private ComponentName remoteComponentName;
    private RemoteControlClient remoteControlClient;
    AudioManager audioManager;

    @Override
    public void onCreate() {
        super.onCreate();

        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "MyPlayer");
        tinyDB = new TinyDB(getApplicationContext());
        editor = getSharedPreferences(MUSIC_PLAYED, MODE_PRIVATE).edit();

        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

//        mediaSessionCompat.setCallback(this);
        mediaSessionCompat.setActive(true);
//        registerRemoteClient();

    }

    private void registerRemoteClient(){
        remoteComponentName = new ComponentName(getApplicationContext(), new NotificationReceiver().ComponentName());
        try {
            if(remoteControlClient == null) {
                audioManager.registerMediaButtonEventReceiver(remoteComponentName);
                Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                mediaButtonIntent.setComponent(remoteComponentName);
                PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
                remoteControlClient = new RemoteControlClient(mediaPendingIntent);
                audioManager.registerRemoteControlClient(remoteControlClient);
            }
            remoteControlClient.setTransportControlFlags(
                    RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
                            RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_STOP |
                            RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS |
                            RemoteControlClient.FLAG_KEY_MEDIA_NEXT);
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public int getAudioSessionId() {
        return mediaPlayer.getAudioSessionId();
    }

    public class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            int myPosition = intent.getIntExtra(servicePosition, -1);
            if (myPosition != -1) playMedia(myPosition);

            String action_name = intent.getStringExtra(actionName);
            if (action_name != null) {
                switch (action_name) {
                    case PLAY:
                        play_pause();
                        if (isplaying()) showNotification(R.drawable.ic_play_24);
                        else showNotification(R.drawable.ic_pause_24);
                        break;
                    case PAUSE:
                        pause();
                        showNotification(R.drawable.ic_pause_24);
                        break;
                    case NEXT:
                        nextSong();
                        break;
                    case PREVIOUS:
                        previousSong();
                        break;
                    case REMOVE:
                        removeClicked();
                        break;
                }
            }

            new Handler().post(this::onCompleted);

        } catch (Exception e) {
            e.printStackTrace();
        }

        MediaButtonReceiver.handleIntent(mediaSessionCompat, intent);

        return START_STICKY;
    }

    private void removeClicked() {
        if (actionPlaying != null) {
            stopForeground(true);
            stopSelf();
            songslist.clear();
            actionPlaying.removeClicked();
        }
    }

    public void previousSong() {
        if (actionPlaying != null) {
            save(position, songslist);
            actionPlaying.previousClicked();
        }
    }

    public void nextSong() {
        if (actionPlaying != null) {
            actionPlaying.nextClicked();
            save(position, songslist);
        }
    }

    public void play_pause() {
        if (actionPlaying != null) actionPlaying.play_pauseClicked();
    }

    public void setLoop(boolean b){
        mediaPlayer.setLooping(b);
    }

    public boolean isLoop(){
        return mediaPlayer.isLooping();
    }

    private void playMedia(int startposition) {
        position = startposition;
        if (mediaPlayer != null) {
            stop();
            release();
            if (songslist != null) {
                create(position);
                start();
            }
        } else {
            create(position);
            start();
        }
    }

    public void start() {
        mediaPlayer.start();
    }

    public void stop() {
        mediaPlayer.stop();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void release() {
        mediaPlayer.release();
    }

    public boolean isplaying() {
        return mediaPlayer.isPlaying();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    public void create(int pos) {
        position = pos;
        uri = Uri.parse(songslist.get(position).getData());
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
        save(position, songslist);
    }

    public void save(int i, ArrayList<Songs> list) {
        TITLE = list.get(i).getName();
        IMAGE = list.get(i).getArtist();
        ARTIST = list.get(i).getImage();
        editor.putString(MUSIC_FILE, String.valueOf(Uri.parse(list.get(i).getData())));
        editor.putString(MUSIC_NAME, list.get(i).getName());
        editor.putString(MUSIC_ARTIST, list.get(i).getArtist());
        editor.putString(MUSIC_IMG, list.get(i).getImage());
        editor.apply();
    }

    public void onCompleted() {
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(mp -> {
                if (actionPlaying != null) {
                    actionPlaying.nextClicked();
                    onCompleted();
                } else {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
            });
        }
    }

    public void setCallback(ActionPlaying playing) {
        this.actionPlaying = playing;
    }


    public void showNotification(int play_pause) {

        Intent prevIntent = new Intent(this, NotificationReceiver.class).setAction(PREVIOUS);
        prevPending = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playIntent = new Intent(this, NotificationReceiver.class).setAction(PLAY);
        playPending = PendingIntent.getBroadcast(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this, NotificationReceiver.class).setAction(NEXT);
        nextPending = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent removeIntent = new Intent(this, NotificationReceiver.class).setAction(REMOVE);
        removePending = PendingIntent.getBroadcast(this, 0, removeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /*Intent replayIntent = new Intent(this, NotificationReceiver.class).setAction(REPLAY_10);
        replayPending = PendingIntent.getBroadcast(this, 0, replayIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent forwardIntent = new Intent(this, NotificationReceiver.class).setAction(FORWARD_10);
        forwardPending = PendingIntent.getBroadcast(this, 0, forwardIntent, PendingIntent.FLAG_UPDATE_CURRENT);
       */
        String picture = songslist.get(position).getImage();
        Bitmap thumbnail = null;
        if (picture != null) {
            try {
                thumbnail = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(picture)));
            } catch (FileNotFoundException e) {
                thumbnail = BitmapFactory.decodeResource(getResources(), R.drawable.music_note_24);
                e.printStackTrace();
            }
        } else thumbnail = BitmapFactory.decodeResource(getResources(), R.drawable.music_note_24);

        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra(current_list, (Serializable) songslist);
        intent.putExtra("pos", position);
        intent.putExtra(duration, getCurrentPosition());
        pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String name = songslist.get(position).getName();
        String artist = songslist.get(position).getArtist();


        if (mediaPlayer.isPlaying()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notification = new Notification.Builder(this, CHANNEL)
                        .setSmallIcon(R.drawable.ic_notify)
                        .setColor(ContextCompat.getColor(this, R.color.blue))
                        .setLargeIcon(thumbnail)
                        .setContentTitle(name)
                        .setContentText(artist)
                        .addAction(R.drawable.ic_skip_previous_24, PREVIOUS, prevPending)
                        .addAction(play_pause, PLAY, playPending)
                        .addAction(R.drawable.ic_skip_next_24, NEXT, nextPending)
                        .setContentIntent(pendingIntent)
                        .setStyle(new Notification.MediaStyle())
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setAutoCancel(false)
                        .setOnlyAlertOnce(true)
                        .build();
            } else {
                belowOreo(name, artist, thumbnail, play_pause);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notification = new Notification.Builder(this, CHANNEL)
                        .setSmallIcon(R.drawable.ic_notify)
                        .setColor(ContextCompat.getColor(this, R.color.blue))
                        .setLargeIcon(thumbnail)
                        .setContentTitle(name)
                        .setContentText(artist)
                        .addAction(R.drawable.ic_skip_previous_24, PREVIOUS, prevPending)
                        .addAction(play_pause, PLAY, playPending)
                        .addAction(R.drawable.ic_skip_next_24, NEXT, nextPending)
                        .addAction(R.drawable.ic_close_24, REMOVE, removePending)
                        .setContentIntent(pendingIntent)
                        .setStyle(new Notification.MediaStyle())
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setOnlyAlertOnce(true)
                        .setAutoCancel(true)
                        .build();
            } else {
                belowOreoRemove(name, artist, thumbnail, play_pause);
            }

        }

        startForeground(11, notification);
    }

    private void belowOreoRemove(String name, String artist, Bitmap thumbnail, int play_pause) {
        notification = new NotificationCompat.Builder(this, CHANNEL)
                .setSmallIcon(R.drawable.ic_notify)
                .setColor(ContextCompat.getColor(this, R.color.blue))
                .setLargeIcon(thumbnail)
                .setContentTitle(name)
                .setContentText(artist)
                .addAction(R.drawable.ic_skip_previous_24, PREVIOUS, prevPending)
                .addAction(play_pause, PLAY, playPending)
                .addAction(R.drawable.ic_skip_next_24, NEXT, nextPending)
                .addAction(R.drawable.ic_close_24, REMOVE, removePending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                .setContentIntent(pendingIntent)
                .setVisibility(VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setSound(null)
                .build();
    }

    private void belowOreo(String name, String artist, Bitmap thumbnail, int play_pause) {
        notification = new NotificationCompat.Builder(this, CHANNEL)
                .setSmallIcon(R.drawable.ic_notify)
                .setColor(ContextCompat.getColor(this, R.color.blue))
                .setLargeIcon(thumbnail)
                .setContentTitle(name)
                .setContentText(artist)
                .addAction(R.drawable.ic_skip_previous_24, PREVIOUS, prevPending)
                .addAction(play_pause, PLAY, playPending)
                .addAction(R.drawable.ic_skip_next_24, NEXT, nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                .setContentIntent(pendingIntent)
                .setVisibility(VISIBILITY_PUBLIC)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setSound(null)
                .build();
    }

}
