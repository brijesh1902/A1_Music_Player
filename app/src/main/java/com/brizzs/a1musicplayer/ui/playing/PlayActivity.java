package com.brizzs.a1musicplayer.ui.playing;

import static com.brizzs.a1musicplayer.utils.Common.MUSIC_NAME;
import static com.brizzs.a1musicplayer.utils.Common.MUSIC_PLAYED;
import static com.brizzs.a1musicplayer.utils.Common.actionName;
import static com.brizzs.a1musicplayer.utils.Common.album;
import static com.brizzs.a1musicplayer.utils.Common.createTime;
import static com.brizzs.a1musicplayer.utils.Common.current_list;
import static com.brizzs.a1musicplayer.utils.Common.duration;
import static com.brizzs.a1musicplayer.utils.Common.isServiceRunning;
import static com.brizzs.a1musicplayer.utils.Common.recently;
import static com.brizzs.a1musicplayer.utils.Common.servicePosition;
import static com.brizzs.a1musicplayer.utils.Common.value;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brizzs.a1musicplayer.adapters.SongsAdapter;
import com.brizzs.a1musicplayer.model.Album;
import com.brizzs.a1musicplayer.service.OnSongAdapterCallback;
import com.brizzs.a1musicplayer.ui.album.AlbumActivity;
import com.brizzs.a1musicplayer.ui.main.MainActivity;
import com.brizzs.a1musicplayer.utils.TinyDB;
import com.brizzs.a1musicplayer.model.Songs;
import com.brizzs.a1musicplayer.R;
import com.brizzs.a1musicplayer.databinding.ActivityPlayBinding;
import com.brizzs.a1musicplayer.service.ActionPlaying;
import com.brizzs.a1musicplayer.service.MusicService;
import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayActivity extends AppCompatActivity implements ActionPlaying, ServiceConnection, OnSongAdapterCallback {

    private static final String TAG = "PlayActivity";
    private static String currentTime = null, endTime = null;
    ImageView play, image, next, previous;
    TextView name, singer;
    SeekBar seekBar;
    public static int position, delay = 500, maxVolume, volume;
    final Handler handler = new Handler();
    TinyDB tinyDB;
    public static ArrayList<Songs> songslist = new ArrayList<>();
    ArrayList<Songs> list = new ArrayList<>();
    Thread updateseek;
    ActivityPlayBinding binding;
    MusicService musicService;
    AudioManager audioManager;
    SharedPreferences preferences;
    SongsAdapter adapter;
    boolean isplaylistOpen = false, isService = false ;
    Animation animation;
    swipeListener swipeListener;
    String actionBack;

    @Override
    public void onBackPressed() {
        if (isplaylistOpen) {
            isplaylistOpen = false;
            binding.rvPlaylist.setVisibility(View.GONE);
            binding.playlist.setBackgroundResource(R.drawable.ic_playlist_play_24);
        } else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            super.onBackPressed();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            supportFinishAfterTransition();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        name = findViewById(R.id.p_name);
        singer = findViewById(R.id.p_artist);
        play = findViewById(R.id.p_play);
        next = findViewById(R.id.p_next);
        previous = findViewById(R.id.p_previous);
        image = findViewById(R.id.img);
        seekBar = findViewById(R.id.seekBar);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_item);
        preferences = getSharedPreferences(MUSIC_PLAYED, MODE_PRIVATE);
        tinyDB = new TinyDB(getApplicationContext());
        isService = isServiceRunning(MusicService.class.getName(), getApplicationContext());

        position = getIntent().getIntExtra("pos", 0);
        actionBack = getIntent().getStringExtra(actionName);
        songslist = (ArrayList<Songs>) getIntent().getSerializableExtra(current_list);

        if (actionBack != null) Log.e("onCreate: ", actionBack);

        audioManager = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        play.setOnClickListener(v -> {
            v.startAnimation(animation);
            play_pauseClicked();
        });

        previous.setOnClickListener(v -> {
            v.startAnimation(animation);
            previousClicked();
        });

        next.setOnClickListener(v -> {
            v.startAnimation(animation);
            nextClicked();
        });

        binding.back.setOnClickListener(v -> {
            v.startAnimation(animation);
            if (isplaylistOpen) {
                isplaylistOpen = false;
                binding.rvPlaylist.setVisibility(View.GONE);
                binding.playlist.setBackgroundResource(R.drawable.ic_playlist_play_24);
            } else {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                supportFinishAfterTransition();
            }
        });

        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.lg_blue), PorterDuff.Mode.SRC_IN);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (musicService != null && fromUser) {
                    musicService.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                musicService.seekTo(seekBar.getProgress());
            }
        });

        swipeListener = new swipeListener(binding.img);
        setPlayname();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.rvPlaylist.setHasFixedSize(true);
        binding.rvPlaylist.setLayoutManager(layoutManager);

        binding.playlist.setOnClickListener(v -> {
            v.startAnimation(animation);
            if (!isplaylistOpen) {
                binding.playlist.setBackgroundResource(R.drawable.ic_baseline_white_playlist_play_24);
                isplaylistOpen = true;
                binding.rvPlaylist.setVisibility(View.VISIBLE);
                adapter = new SongsAdapter(this, songslist, recently);
                binding.rvPlaylist.setAdapter(adapter);
            } else {
                binding.playlist.setBackgroundResource(R.drawable.ic_playlist_play_24);
                isplaylistOpen = false;
                binding.rvPlaylist.setVisibility(View.GONE);
            }
        });

        binding.shuffle.setOnClickListener(v -> {
            v.startAnimation(animation);
            list.clear();
            list.addAll(songslist);
            Collections.shuffle(list);
            adapter = new SongsAdapter(this, list, recently);
            binding.rvPlaylist.setAdapter(adapter);
        });

        binding.forward.setOnClickListener(v -> {
            v.startAnimation(animation);
            int p = musicService.getCurrentPosition() + 10000;
            if (p <= musicService.getDuration()) {
                currentTime = createTime(p);
                musicService.seekTo(p);
            }
        });

        binding.replay.setOnClickListener(v -> {
            v.startAnimation(animation);
            int p = musicService.getCurrentPosition() - 10000;
            if (p >= 0) {
                currentTime = createTime(p);
                musicService.seekTo(p);
            }
        });

    }

    @Override
    public void Callback(int adapterPosition, List<Songs> data, ImageView image, TextView name, TextView singer) {
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("pos", adapterPosition);
        intent.putExtra(duration, "0");
        intent.putExtra(current_list, (Serializable) data);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Pair<View, String> pair1 = Pair.create(image, "image");
        Pair<View, String> pair2 = Pair.create(name, "songname");
        Pair<View, String> pair3 = Pair.create(singer, "singer");
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                .makeSceneTransitionAnimation(PlayActivity.this, pair1, pair2, pair3);

        startActivity(intent, optionsCompat.toBundle());
    }

    @Override
    public void AlbumCallback(int adapterPosition, List<Album> data, ImageView image, TextView name, TextView singer) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isService) {
            start_service();
        } else if (!songslist.get(position).getName().equals(preferences.getString(MUSIC_NAME, null))){
            start_service();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        if (musicService != null) musicService.onCompleted();
    }

    private void start_service() {
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra(servicePosition, position);
        intent.putExtra(current_list, (Serializable) songslist);
        startService(intent);
    }

    private void setPlayname() {

//        setView(getApplicationContext(), binding.pName, binding.pArtist, binding.img);
        Glide.with(getApplicationContext()).load(songslist.get(position).getImage())
                .placeholder(R.drawable.music_note_24)
                .error(R.drawable.music_note_24)
                .into(image);

        binding.pName.setText(songslist.get(position).getName());
        binding.pArtist.setText(songslist.get(position).getArtist());

        play.setImageResource(R.drawable.ic_play_24);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               if (musicService != null) {
                   endTime = createTime(musicService.getDuration());
                   binding.endTime.setText(endTime);

                   currentTime = createTime(musicService.getCurrentPosition());
                   binding.startTime.setText(currentTime);
                   handler.postDelayed(this, delay);
               }
            }
        }, delay);

        updateseek = new Thread() {
            @Override
            public void run() {
                int cp = 0;
                try {
                    while (cp < musicService.getDuration()) {
                        cp = musicService.getCurrentPosition();
                        seekBar.setProgress(cp);
                        sleep(delay);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        updateseek.start();

        binding.pName.setSelected(true);
        binding.pArtist.setSelected(true);

    }

    /*private void setSongGradient() {
        byte[] img = getImage(songslist.get(position).getData());
        Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@Nullable Palette palette) {
                Palette.Swatch swatch = palette.getDominantSwatch();
                if (swatch != null) {
                    binding.gradient.setBackgroundResource(R.drawable.bg_gradient);
                    GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                            new int[]{swatch.getRgb(), 0x00000000});
                    binding.gradient.setBackground(gradientDrawable);
                    binding.pName.setTextColor(Color.WHITE);
                    binding.pArtist.setTextColor(Color.DKGRAY);
                }
            }
        });
    }*/

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder binder = (MusicService.MyBinder) service;
        musicService = binder.getService();
        musicService.setCallback(this);

        setPlayname();
        musicService.onCompleted();
        seekBar.setMax(musicService.getDuration());
        if (musicService.isplaying()) binding.pPlay.setImageResource(R.drawable.ic_play_24);
        else binding.pPlay.setImageResource(R.drawable.ic_pause_24);
        musicService.showNotification(R.drawable.ic_play_24);

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
        value = null;
    }

    @Override
    public void play_pauseClicked() {
        if (musicService != null) {
            if (musicService.isplaying()) {
                play.setImageResource(R.drawable.ic_pause_24);
                musicService.pause();
                musicService.showNotification(R.drawable.ic_pause_24);
            } else {
                play.setImageResource(R.drawable.ic_play_24);
                musicService.start();
                musicService.showNotification(R.drawable.ic_play_24);
            }
        }
    }

    @Override
    public void nextClicked() {
        if (musicService != null) {
            musicService.stop();
            musicService.release();

            if (songslist.size() > 0)
                if (position < songslist.size() - 1)
                    position++;
                else
                    position = 0;

            musicService.create(position);
            musicService.start();
            seekBar.setProgress(musicService.getCurrentPosition());
            seekBar.setMax(musicService.getDuration());
            musicService.showNotification(R.drawable.ic_play_24);
            setPlayname();
        }
    }

    @Override
    public void previousClicked() {
        if (musicService != null) {
            if (musicService.getCurrentPosition() >= 10000) {
                musicService.stop();
                musicService.release();
                musicService.create(position);
                musicService.start();
            } else {
                musicService.stop();
                musicService.release();

                if (songslist.size() > 0)
                    if (position > 0) position--;
                    else position = songslist.size() - 1;

                musicService.create(position);
                musicService.start();
                seekBar.setProgress(musicService.getCurrentPosition());
                seekBar.setMax(musicService.getDuration());
            }
            musicService.showNotification(R.drawable.ic_play_24);
            setPlayname();
        }
    }

    @Override
    public void removeClicked() {
        unbindService(this);
        finish();
    }

    private class swipeListener implements View.OnTouchListener {

        GestureDetector gestureDetector;
        int volumeUp, volumeDown, ver_divider, threshold = 100, vel_threshold = 100;

        swipeListener(View view) {
            GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    ver_divider = getWindow().getDecorView().getWidth() / 2;

                    float xDiff = e2.getX() - e1.getX();
                    float yDiff = e2.getY() - e1.getY();

                 /*   if (Math.abs(xDiff) > threshold && Math.abs(distanceX) > vel_threshold) {
                        if (xDiff < 0) {
                            nextClicked(); // right
                            Toast.makeText(PlayActivity.this, "NEXT", Toast.LENGTH_SHORT).show();
                        } else {
                            previousClicked(); // left
                            Toast.makeText(PlayActivity.this, "PREVIOUS", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                  if (Math.abs(xDiff) > Math.abs(yDiff)) {

                  }*/

                    if (e1.getX() > ver_divider) {
                        if (e1.getY() < e2.getY()) {
                            volumeUp = 0;
                            volumeDown += 1;
                            if (volumeDown > 5) {
                                audioManager.adjustVolume( AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
                                volumeDown = 0;
                            }
                        } else if (e1.getY() > e2.getY()) {
                            volumeDown = 0;
                            volumeUp += 1;
                            if (volumeUp > 5) {
                                audioManager.adjustVolume( AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                                volumeUp = 0;
                            }
                        }
                        binding.volume.setVisibility(View.VISIBLE);
                    }

                    volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    binding.volume.setText(String.valueOf(volume));
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.volume.setVisibility(View.GONE);
                        }
                    }, delay);

                    return false;
                }
            };

            gestureDetector = new GestureDetector(listener);
            view.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }
    }

}