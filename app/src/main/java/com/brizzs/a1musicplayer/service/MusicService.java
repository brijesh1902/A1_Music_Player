package com.brizzs.a1musicplayer.service;

import static android.app.Notification.CATEGORY_CALL;
import static androidx.core.app.NotificationCompat.PRIORITY_MAX;
import static androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC;
import static com.brizzs.a1musicplayer.utils.MyApplication.CHANNEL;
import static com.brizzs.a1musicplayer.utils.MyApplication.NEXT;
import static com.brizzs.a1musicplayer.utils.MyApplication.PAUSE;
import static com.brizzs.a1musicplayer.utils.MyApplication.PLAY;
import static com.brizzs.a1musicplayer.utils.MyApplication.PREVIOUS;
import static com.brizzs.a1musicplayer.utils.MyApplication.REMOVE;
import static com.brizzs.a1musicplayer.ui.playing.PlayActivity.songsList;
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.media.session.MediaButtonReceiver;

import com.brizzs.a1musicplayer.model.Songs;
import com.brizzs.a1musicplayer.R;
import com.brizzs.a1musicplayer.ui.playing.FullScreenActivity;
import com.brizzs.a1musicplayer.ui.playing.PlayActivity;
import com.brizzs.a1musicplayer.utils.TinyDB;
import com.google.android.exoplayer2.ExoPlayer;

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
    MediaSession mediaSession;
    Notification notification = null;
    TinyDB tinyDB;
    SharedPreferences.Editor editor;
    PendingIntent prevPending, playPending, nextPending, removePending, replayPending, forwardPending, pendingIntent, fullScreenPendingIntent;
    private MediaController mController;
    private PlaybackState mPlaybackState;
    private MediaMetadata mMetadata;

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        mediaSession = new MediaSession(getApplicationContext(), "MyPlayer");
        mediaSessionCompat = new MediaSessionCompat(getApplicationContext(), "MyPlayer");
        tinyDB = new TinyDB(getApplicationContext());
        editor = getSharedPreferences(MUSIC_PLAYED, MODE_PRIVATE).edit();

        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mediaSessionCompat.setActive(true);
        mediaSession.setActive(true);

        mController = new MediaController(getApplicationContext(), mediaSession.getSessionToken());

    }

    private final MediaController.Callback mCb = new MediaController.Callback() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackState state) {
            mPlaybackState = state;
        }

        @Override
        public void onMetadataChanged(MediaMetadata metadata) {
            mMetadata = metadata;
        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
        }
    };

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
        mController.unregisterCallback(mCb);
        stopForeground(true);
        stopSelf();
        actionPlaying.removeClicked();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mediaSessionCompat, intent);

        int myPosition = intent.getIntExtra(servicePosition, -1);
        if (myPosition != -1)
            playMedia(myPosition);

        try {

            String action_name = intent.getStringExtra(actionName);
            if (action_name != null) {
                switch (action_name) {
                    case PLAY:
                        play_pause();
                        if (isPlaying()) showNotification(R.drawable.ic_play_24, 1f);
                        else showNotification(R.drawable.ic_pause_24, 0f);
                        break;
                    case PAUSE:
                        pause();
                        showNotification(R.drawable.ic_pause_24, 0f);
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

        return START_STICKY;
    }

    private void removeClicked() {
        if (actionPlaying != null) {
            mController.unregisterCallback(mCb);
            stopForeground(true);
            stopSelf();
            actionPlaying.removeClicked();
        }
    }

    public void previousSong() {
        if (actionPlaying != null) {
            save(position, songsList);
            actionPlaying.previousClicked();
        }
    }

    public void nextSong() {
        if (actionPlaying != null) {
            actionPlaying.nextClicked();
            save(position, songsList);
        }
    }

    public void play_pause() {
        if (actionPlaying != null)
            actionPlaying.play_pauseClicked();
    }

    public void setLoop(boolean b) {
        mediaPlayer.setLooping(b);
    }

    public boolean isLoop() {
        return mediaPlayer.isLooping();
    }

    private void playMedia(int start_position) {
        position = start_position;
        if (mediaPlayer != null) {
            stop();
            release();
            if (songsList != null) {
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

    public boolean isPlaying() {
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
        uri = Uri.parse(songsList.get(position).getData());
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
        save(position, songsList);
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


    public void showNotification(int play_pause, float speed) {

        mediaSession.setMetadata(new MediaMetadata.Builder().putLong(MediaMetadata.METADATA_KEY_DURATION, mediaPlayer.getDuration()).build());

        mediaSession.setPlaybackState(new PlaybackState.Builder()
                .setState(PlaybackState.STATE_PLAYING,
                        mediaPlayer.getCurrentPosition(),
                        isPlaying() ? 1f : 0f,
                        SystemClock.elapsedRealtime())
//                .setActions(PlaybackState.ACTION_SEEK_TO)
                .build());

        mPlaybackState = mController.getPlaybackState();
        mController.registerCallback(mCb);

        Intent prevIntent = new Intent(this, NotificationReceiver.class).setAction(PREVIOUS);
        prevPending = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent playIntent = new Intent(this, NotificationReceiver.class).setAction(PLAY);
        playPending = PendingIntent.getBroadcast(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent nextIntent = new Intent(this, NotificationReceiver.class).setAction(NEXT);
        nextPending = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent removeIntent = new Intent(this, NotificationReceiver.class).setAction(REMOVE);
        removePending = PendingIntent.getBroadcast(this, 0, removeIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        /*Intent replayIntent = new Intent(this, NotificationReceiver.class).setAction(REPLAY_10);
        replayPending = PendingIntent.getBroadcast(this, 0, replayIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_MUTABLE);

        Intent forwardIntent = new Intent(this, NotificationReceiver.class).setAction(FORWARD_10);
        forwardPending = PendingIntent.getBroadcast(this, 0, forwardIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_MUTABLE);
       */


        Intent fullScreenIntent = new Intent(new Intent(this, FullScreenActivity.class));
        fullScreenPendingIntent = PendingIntent.getActivity(this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        String picture = songsList.get(PlayActivity.position).getImage();
        Bitmap thumbnail;
        if (picture != null) {
            try {
                thumbnail = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(picture)));
            } catch (FileNotFoundException e) {
                thumbnail = BitmapFactory.decodeResource(getResources(), R.drawable.music_note_24);
                e.printStackTrace();
            }
        } else thumbnail = BitmapFactory.decodeResource(getResources(), R.drawable.music_note_24);

        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra(current_list, (Serializable) songsList);
        intent.putExtra("pos", PlayActivity.position);
        intent.putExtra(duration, getCurrentPosition());
        pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        String name = songsList.get(PlayActivity.position).getName();
        String artist = songsList.get(PlayActivity.position).getArtist();


        if (mediaPlayer.isPlaying()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notification = new Notification.Builder(this, CHANNEL)
                        .setSmallIcon(R.drawable.ic_notify)
                        .setColor(ContextCompat.getColor(this, R.color.primary))
                        .setLargeIcon(thumbnail)
                        .setContentTitle(name)
                        .setContentText(artist)
//                        .addAction(new Notification.Action(R.drawable.ic_skip_previous_24, PREVIOUS, MediaButtonReceiver.buildMediaButtonPendingIntent(getApplicationContext(), PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)))
                        .addAction(R.drawable.ic_skip_previous_24, PREVIOUS, prevPending)
                        .addAction(play_pause, PLAY, playPending)
                        .addAction(R.drawable.ic_skip_next_24, NEXT, nextPending)
                        .setContentIntent(pendingIntent)
//                        .setStyle(new Notification.MediaStyle())
                        .setStyle(new Notification.MediaStyle()
//                                .setShowActionsInCompactView(0, 1, 2)
                                .setMediaSession(mediaSession.getSessionToken()))
                        .setShowWhen(true)
                        .setFullScreenIntent(fullScreenPendingIntent, true)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setCategory(CATEGORY_CALL)
                        .setPublicVersion(notification)
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
                        .setColor(ContextCompat.getColor(this, R.color.primary))
                        .setLargeIcon(thumbnail)
                        .setContentTitle(name)
                        .setContentText(artist)
//                        .addAction(new Notification.Action(R.drawable.ic_skip_previous_24, PREVIOUS, MediaButtonReceiver.buildMediaButtonPendingIntent(getApplicationContext(), PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)))
                        .addAction(R.drawable.ic_skip_previous_24, PREVIOUS, prevPending)
                        .addAction(play_pause, PLAY, playPending)
                        .addAction(R.drawable.ic_skip_next_24, NEXT, nextPending)
                        .addAction(R.drawable.ic_close_24, REMOVE, removePending)
                        .setContentIntent(pendingIntent)
//                        .setStyle(new Notification.MediaStyle())
                        .setStyle(new Notification.MediaStyle()
//                                .setShowActionsInCompactView(0, 1, 2)
                                .setMediaSession(mediaSession.getSessionToken()))
                        .setFullScreenIntent(fullScreenPendingIntent, true)
                        .setShowWhen(true)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setCategory(CATEGORY_CALL)
                        .setPublicVersion(notification)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setOnlyAlertOnce(true)
                        .setOngoing(true)
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
                .setColor(ContextCompat.getColor(this, R.color.primary))
                .setLargeIcon(thumbnail)
                .setContentTitle(name)
                .setContentText(artist)
                .addAction(R.drawable.ic_skip_previous_24, PREVIOUS, prevPending)
                .addAction(play_pause, PLAY, playPending)
                .addAction(R.drawable.ic_skip_next_24, NEXT, nextPending)
                .addAction(R.drawable.ic_close_24, REMOVE, removePending)
//                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setShowWhen(true)
                .setContentIntent(pendingIntent)
                .setVisibility(VISIBILITY_PUBLIC)
                .setCategory(CATEGORY_CALL)
                .setPublicVersion(notification)
                .setPriority(PRIORITY_MAX)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setSound(null)
                .build();
    }

    private void belowOreo(String name, String artist, Bitmap thumbnail, int play_pause) {
        notification = new NotificationCompat.Builder(this, CHANNEL)
                .setSmallIcon(R.drawable.ic_notify)
                .setColor(ContextCompat.getColor(this, R.color.primary))
                .setLargeIcon(thumbnail)
                .setContentTitle(name)
                .setContentText(artist)
                .addAction(R.drawable.ic_skip_previous_24, PREVIOUS, prevPending)
                .addAction(play_pause, PLAY, playPending)
                .addAction(R.drawable.ic_skip_next_24, NEXT, nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setShowWhen(true)
                .setContentIntent(pendingIntent)
                .setVisibility(VISIBILITY_PUBLIC)
                .setCategory(CATEGORY_CALL)
                .setPublicVersion(notification)
                .setPriority(PRIORITY_MAX)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setSound(null)
                .build();
    }

}
