package com.brizzs.a1musicplayer.ui.playing;

import static com.brizzs.a1musicplayer.utils.Common.MUSIC_NAME;
import static com.brizzs.a1musicplayer.utils.Common.MUSIC_PLAYED;
import static com.brizzs.a1musicplayer.utils.Common.SHOW_MINI_PLAYER;
import static com.brizzs.a1musicplayer.utils.Common.SPAN_COUNT;
import static com.brizzs.a1musicplayer.utils.Common.actionName;
import static com.brizzs.a1musicplayer.utils.Common.createTime;
import static com.brizzs.a1musicplayer.utils.Common.current_list;
import static com.brizzs.a1musicplayer.utils.Common.duration;
import static com.brizzs.a1musicplayer.utils.Common.isServiceRunning;
import static com.brizzs.a1musicplayer.utils.Common.recently;
import static com.brizzs.a1musicplayer.utils.Common.servicePosition;
import static com.brizzs.a1musicplayer.utils.Common.value;
import static com.brizzs.a1musicplayer.utils.Const.SONG_ARTIST;
import static com.brizzs.a1musicplayer.utils.Const.SONG_IMAGE;
import static com.brizzs.a1musicplayer.utils.Const.SONG_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.graphics.Insets;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.os.AsyncTask;
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
import com.brizzs.a1musicplayer.dao.SongsDao;
import com.brizzs.a1musicplayer.db.SongsDB;
import com.brizzs.a1musicplayer.model.Album;
import com.brizzs.a1musicplayer.service.OnSongAdapterCallback;
import com.brizzs.a1musicplayer.ui.main.MainActivity;
import com.brizzs.a1musicplayer.utils.SharePreference;
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
    SharePreference sharePreference;
    public static ArrayList<Songs> songsList = new ArrayList<>();
    ArrayList<Songs> list = new ArrayList<>();
    Thread updateSeek;
    ActivityPlayBinding binding;
    MusicService musicService;
    AudioManager audioManager;
    SharedPreferences preferences;
    SongsAdapter adapter;
    boolean isPlayListOpen = false, isService = false, isFavourite = false ;
    Animation animation;
    swipeListener swipeListener;
    String actionBack;
    GridLayoutManager layoutManager;
    private SongsDao songsDao;

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        if (isPlayListOpen) {
            isPlayListOpen = false;
            binding.rvPlaylist.setVisibility(View.GONE);
            binding.materialCardView5.setVisibility(View.VISIBLE);
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

        // Look for and REMOVE or comment out this block:
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            // This code typically applies padding equal to the system bar height
            // to prevent content from going behind the bars.
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Removing the logic that sets padding here will also help disable the effect.
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });

        name = findViewById(R.id.p_name);
        singer = findViewById(R.id.p_artist);
        play = findViewById(R.id.p_play);
        next = findViewById(R.id.p_next);
        previous = findViewById(R.id.p_previous);
        image = findViewById(R.id.img);
        seekBar = findViewById(R.id.seekBar);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_item);
        preferences = getSharedPreferences(MUSIC_PLAYED, MODE_PRIVATE);
        sharePreference = new SharePreference(getApplicationContext());

        isService = isServiceRunning(MusicService.class.getName(), getApplicationContext());

        position = getIntent().getIntExtra("pos", 0);
        actionBack = getIntent().getStringExtra(actionName);

        songsList = (ArrayList<Songs>) getIntent().getSerializableExtra(current_list);

        SongsDB db = SongsDB.getDatabase(getApplicationContext());
        songsDao = db.songsDao();

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

        layoutManager = new GridLayoutManager(getApplicationContext(), SPAN_COUNT);
        binding.rvPlaylist.setHasFixedSize(true);
        binding.rvPlaylist.setLayoutManager(layoutManager);
        binding.rvPlaylist.setItemAnimator(null);

    }


    @Override
    public void Callback(int adapterPosition, List<Songs> data, ImageView image, TextView name, TextView singer) {
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("pos", adapterPosition);
        intent.putExtra(duration, "0");
        intent.putExtra(current_list, (Serializable) data);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Pair<View, String> pair1 = Pair.create(image, SONG_IMAGE);
        Pair<View, String> pair2 = Pair.create(name, SONG_NAME);
        Pair<View, String> pair3 = Pair.create(singer, SONG_ARTIST);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(PlayActivity.this, pair1, pair2, pair3);

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
        } else if (!songsList.get(position).getName().equals(preferences.getString(MUSIC_NAME, null))){
            start_service();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);

        if (musicService != null) {
            musicService.onCompleted();
        }

        checkFavourites();

        binding.back.setOnClickListener(v -> {
//            v.startAnimation(animation);
//            if (isPlayListOpen) {
//                isPlayListOpen = false;
//                binding.rvPlaylist.setVisibility(View.GONE);
//                binding.materialCardView5.setVisibility(View.VISIBLE);
//                binding.playlist.setBackgroundResource(R.drawable.ic_playlist_play_24);
//            } else {
//                startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                supportFinishAfterTransition();
//            }
            onBackPressed();
        });

        binding.playlist.setOnClickListener(v -> {
            v.startAnimation(animation);
            if (!isPlayListOpen) {
                binding.playlist.setBackgroundResource(R.drawable.ic_baseline_white_playlist_play_24);
                binding.materialCardView5.setVisibility(View.GONE);
                binding.rvPlaylist.setVisibility(View.VISIBLE);
                adapter = new SongsAdapter(this, songsList, recently, layoutManager);
                binding.rvPlaylist.setAdapter(adapter);
                binding.rvPlaylist.post(()->binding.rvPlaylist.scrollToPosition(position));
                isPlayListOpen = true;
            } else {
                binding.playlist.setBackgroundResource(R.drawable.ic_playlist_play_24);
                binding.materialCardView5.setVisibility(View.VISIBLE);
                binding.rvPlaylist.setVisibility(View.GONE);
                isPlayListOpen = false;
            }
        });

        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                Collections.swap(songsList, viewHolder.getAbsoluteAdapterPosition(), target.getAbsoluteAdapterPosition());
                adapter.notifyItemMoved(viewHolder.getAbsoluteAdapterPosition(), target.getAbsoluteAdapterPosition());
                binding.rvPlaylist.invalidate();
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                        ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
            }
        });
        touchHelper.attachToRecyclerView(binding.rvPlaylist);

        binding.shuffle.setOnClickListener(v -> {
            v.startAnimation(animation);
            list.clear();
            list.addAll(songsList);
            Collections.shuffle(list);
            adapter = new SongsAdapter(this, list, recently, layoutManager);
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

        binding.loop.setOnClickListener(v -> {
            v.startAnimation(animation);
            if (musicService.isLoop()){
                musicService.setLoop(false);
                binding.loop.setBackgroundResource(R.drawable.ic_baseline_loop_blue_24);
            } else {
                musicService.setLoop(true);
                binding.loop.setBackgroundResource(R.drawable.ic_baseline_loop_24);
            }
        });

        binding.btnFavourite.setOnClickListener(v -> {
            v.startAnimation(animation);
            if (isFavourite){
                new DeleteTask(songsDao).execute(songsList.get(position));
                binding.btnFavourite.setImageResource(R.drawable.ic_like);
                Toast.makeText(this, songsList.get(position).getName()+" removed from favourites.", Toast.LENGTH_SHORT).show();
                isFavourite = false;
            } else {
                new InsertTask(songsDao).execute(songsList.get(position));
                binding.btnFavourite.setImageResource(R.drawable.ic_like_color);
                Toast.makeText(this, songsList.get(position).getName()+" added to favourites.", Toast.LENGTH_SHORT).show();
                isFavourite = true;
            }
            binding.btnFavourite.invalidate();
        });


    }

    private void checkFavourites() {
        songsDao.getSongs().observe(PlayActivity.this, songs -> {
            if (songs.size() > 0) {
                for (Songs s : songs) {
                    if (s.getName().equals(songsList.get(position).getName())) {
                        isFavourite = true;
                        binding.btnFavourite.setImageResource(R.drawable.ic_like_color);
                    } else {
                        isFavourite = false;
                        binding.btnFavourite.setImageResource(R.drawable.ic_like);
                    }
                }
            } else {
                isFavourite = false;
                binding.btnFavourite.setImageResource(R.drawable.ic_like);
            }
            binding.btnFavourite.invalidate();
            Log.e( "observe: ", isFavourite+"");
        });
    }

    private void start_service() {
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra(servicePosition, position);
        intent.putExtra(current_list, songsList);
        startService(intent);
    }

    private void setPlayname() {

//        setView(getApplicationContext(), binding.pName, binding.pArtist, binding.img);
        Glide.with(getApplicationContext()).load(songsList.get(position).getImage())
                .placeholder(R.drawable.music_note_24)
                .error(R.drawable.music_note_24)
                .into(image);

        binding.pName.setText(songsList.get(position).getName());
        binding.pArtist.setText(songsList.get(position).getArtist());

        play.setImageResource(R.drawable.ic_play_24);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               if (musicService != null && musicService.isPlaying()) {
                   endTime = createTime(musicService.getDuration());
                   binding.endTime.setText(endTime);

                   currentTime = createTime(musicService.getCurrentPosition());
                   binding.startTime.setText(currentTime);
                   handler.postDelayed(this, delay);

                 /*  if (musicService.isLoop()) {
                       binding.loop.setBackgroundResource(R.drawable.ic_baseline_loop_24);
                   } else {
                       binding.loop.setBackgroundResource(R.drawable.ic_baseline_loop_blue_24);
                   }*/
               }
            }
        }, delay);

        updateSeek = new Thread() {
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
        updateSeek.start();

        binding.loop.setBackgroundResource(R.drawable.ic_baseline_loop_blue_24);
        binding.pName.setSelected(true);
        binding.pArtist.setSelected(true);

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder binder = (MusicService.MyBinder) service;
        musicService = binder.getService();
        musicService.setCallback(this);

        setPlayname();
        musicService.onCompleted();
        try {
            seekBar.setMax(musicService.getDuration());
            if (musicService.isPlaying()) binding.pPlay.setImageResource(R.drawable.ic_play_24);
            else binding.pPlay.setImageResource(R.drawable.ic_pause_24);
            musicService.showNotification(R.drawable.ic_play_24, 1f);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
        value = null;
        SHOW_MINI_PLAYER = false;
    }

    @Override
    public void play_pauseClicked() {
        if (musicService != null) {
            if (musicService.isPlaying()) {
                play.setImageResource(R.drawable.ic_pause_24);
                musicService.pause();
                musicService.showNotification(R.drawable.ic_pause_24, 0f);
            } else {
                play.setImageResource(R.drawable.ic_play_24);
                musicService.start();
                musicService.showNotification(R.drawable.ic_play_24, 1f);
            }
        }
    }

    @Override
    public void nextClicked() {
        if (musicService != null) {
//            if (musicService.isLoop()) musicService.setLoop(false);
            musicService.stop();
            musicService.release();

            if (songsList.size() > 0)
                if (position < songsList.size() - 1)
                    position++;
                else
                    position = 0;

            musicService.create(position);
            musicService.start();
            seekBar.setProgress(musicService.getCurrentPosition());
            seekBar.setMax(musicService.getDuration());
            musicService.showNotification(R.drawable.ic_play_24, 1f);
            setPlayname();
        }
        checkFavourites();
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
//                if (musicService.isLoop()) musicService.setLoop(false);
                musicService.stop();
                musicService.release();

                if (songsList.size() > 0)
                    if (position > 0) position--;
                    else position = songsList.size() - 1;

                musicService.create(position);
                musicService.start();
                seekBar.setProgress(musicService.getCurrentPosition());
                seekBar.setMax(musicService.getDuration());
            }
            musicService.showNotification(R.drawable.ic_play_24, 1f);
            setPlayname();
        }
        checkFavourites();
    }

    @Override
    public void removeClicked() {
        musicService.stop();
        musicService.release();
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
                    handler.postDelayed(() -> binding.volume.setVisibility(View.GONE), delay);

                    return false;
                }
            };

            gestureDetector = new GestureDetector(listener);
            view.setOnTouchListener(this);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }
    }

    private static class InsertTask extends AsyncTask<Songs, Void, Void> {
        private final SongsDao dao;
        public InsertTask(SongsDao a) {
            this.dao = a;
        }

        @Override
        protected Void doInBackground(Songs... playLists) {
            try {
                SongsDB.databaseWriteExecutor.execute(() -> {
                    dao.insert(playLists[0]);
                });
                Log.e("doInBackground0: ", playLists[0].getName());
            } catch (Exception e) {
                Log.i("doInBackground2: ", e.toString());
            }
            return null;
        }
    }

    private static class DeleteTask extends AsyncTask<Songs, Void, Void> {
        private final SongsDao dao;
        public DeleteTask(SongsDao a) {
            this.dao = a;
        }

        @Override
        protected Void doInBackground(Songs... playLists) {
            try {
                SongsDB.databaseWriteExecutor.execute(() -> {
                    dao.delete(playLists[0]);
                });
                Log.e("doInBackground0: ", String.valueOf(playLists[0].getName()));
            } catch (Exception e) {
                Log.i("doInBackground2: ", e.toString());
            }
            return null;
        }
    }

}