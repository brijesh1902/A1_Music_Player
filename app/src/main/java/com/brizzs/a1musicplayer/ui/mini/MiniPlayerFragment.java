package com.brizzs.a1musicplayer.ui.mini;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Context.MODE_PRIVATE;
import static com.brizzs.a1musicplayer.ui.playing.PlayActivity.position;
import static com.brizzs.a1musicplayer.ui.playing.PlayActivity.songsList;
import static com.brizzs.a1musicplayer.utils.Common.MUSIC_ARTIST;
import static com.brizzs.a1musicplayer.utils.Common.MUSIC_IMG;
import static com.brizzs.a1musicplayer.utils.Common.MUSIC_NAME;
import static com.brizzs.a1musicplayer.utils.Common.MUSIC_PLAYED;
import static com.brizzs.a1musicplayer.utils.Common.SHOW_MINI_PLAYER;
import static com.brizzs.a1musicplayer.utils.Common.artist;
import static com.brizzs.a1musicplayer.utils.Common.createTime;
import static com.brizzs.a1musicplayer.utils.Common.current_list;
import static com.brizzs.a1musicplayer.utils.Common.duration;
import static com.brizzs.a1musicplayer.utils.Common.image;
import static com.brizzs.a1musicplayer.utils.Common.isServiceRunning;
import static com.brizzs.a1musicplayer.utils.Common.name;
import static com.brizzs.a1musicplayer.utils.Const.SONG_ARTIST;
import static com.brizzs.a1musicplayer.utils.Const.SONG_IMAGE;
import static com.brizzs.a1musicplayer.utils.Const.SONG_NAME;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.brizzs.a1musicplayer.R;
import com.brizzs.a1musicplayer.databinding.FragmentMiniPlayerBinding;
import com.brizzs.a1musicplayer.model.Songs;
import com.brizzs.a1musicplayer.service.ActionPlaying;
import com.brizzs.a1musicplayer.service.MusicService;
import com.brizzs.a1musicplayer.ui.playing.PlayActivity;
import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.ArrayList;

public class MiniPlayerFragment extends Fragment implements ServiceConnection, ActionPlaying {

    private static final String TAG = "Fragment";
    String currentTime = null;
    MusicService musicService;
    FragmentMiniPlayerBinding binding;
    SharedPreferences preferences;
    Intent ser_intent;
    Thread updateseek;
    View view;
    int delay = 500;
    Animation animation;
    boolean isService;
    ArrayList<Songs> list = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMiniPlayerBinding.inflate(getLayoutInflater());
        view = binding.getRoot();

        isService = isServiceRunning(MusicService.class.getName(), view.getContext());
        preferences = view.getContext().getSharedPreferences(MUSIC_PLAYED, MODE_PRIVATE);
        ser_intent = new Intent(getContext(), MusicService.class);

        animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_item);

        if (isService)
            start_service();
        else {
            binding.parent.setVisibility(View.GONE);
//            binding.adView.setVisibility(View.VISIBLE);
        }

//        AdRequest adRequest = new AdRequest.Builder().build();
//        binding.adView.loadAd(adRequest);

        binding.name.setSelected(true);

        binding.parent.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PlayActivity.class);
            intent.putExtra(current_list, (Serializable) songsList);
            intent.putExtra("pos", musicService.position);
            intent.putExtra(duration, musicService.getCurrentPosition());

            Pair<View, String> pair1 = Pair.create(binding.img, SONG_IMAGE);
            Pair<View, String> pair2 = Pair.create(binding.name, SONG_NAME);
            Pair<View, String> pair3 = Pair.create(binding.artist, SONG_ARTIST);

            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), pair1, pair2, pair3);

            requireActivity().startActivity(intent, optionsCompat.toBundle());
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

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

        binding.next.setOnClickListener(v -> {
            v.startAnimation(animation);
            nextClicked();
        });

        binding.prev.setOnClickListener(v -> {
            v.startAnimation(animation);
            previousClicked();
        });

        binding.play.setOnClickListener(v -> {
            v.startAnimation(animation);
            play_pauseClicked();
        });

        if (musicService != null && !musicService.isPlaying()) {
            requireActivity().getSupportFragmentManager().beginTransaction().detach(this).commit();
        }

    }

    private void start_service() {
        if (getContext() != null) getContext().bindService(ser_intent, this, BIND_AUTO_CREATE);
        binding.parent.setVisibility(View.VISIBLE);
        setPlay();
    }


    public void setPlay() {
        getValues();

        try {
            Glide.with(requireActivity()).load(image)
                    .placeholder(R.drawable.music_note_24)
                    .error(R.drawable.music_note_24)
                    .into(binding.img);

            binding.name.setText(name);
            binding.artist.setText(artist);

            updateseek = new Thread() {
                @Override
                public void run() {
                    int cp = 0;
                    try {
                        while (cp < musicService.getDuration()) {
                            cp = musicService.getCurrentPosition();
                            binding.indicator.setProgressCompat(cp, true);
                            sleep(delay);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            updateseek.start();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void getValues() {
        name = preferences.getString(MUSIC_NAME, null);
        artist = preferences.getString(MUSIC_ARTIST, null);
        image = preferences.getString(MUSIC_IMG, null);
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder binder = (MusicService.MyBinder) service;
        musicService = binder.getService();
        musicService.setCallback(this);
        
        list = songsList;

        setPlay();
        musicService.onCompleted();
        binding.indicator.setMax(musicService.getDuration());

        if (musicService.isPlaying()) binding.play.setImageResource(R.drawable.ic_play_24);
        else binding.play.setImageResource(R.drawable.ic_pause_24);

        currentTime = createTime(musicService.getCurrentPosition());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
        SHOW_MINI_PLAYER = false;
    }

    public void play_pauseClicked() {
        if (musicService != null) {
            if (musicService.isPlaying()) {
                binding.play.setImageResource(R.drawable.ic_pause_24);
                musicService.pause();
                musicService.showNotification(R.drawable.ic_pause_24, 0f);
            } else {
                binding.play.setImageResource(R.drawable.ic_play_24);
                musicService.start();
                musicService.showNotification(R.drawable.ic_play_24, 1f);
            }
        }
    }


    public void nextClicked() {
        if (musicService != null) {
            musicService.stop();
            musicService.release();

            if (list.size() > 0)
                if (position < list.size() - 1)
                    position++;
                else
                    position = 0;

            musicService.create(position);
            musicService.start();
            musicService.showNotification(R.drawable.ic_play_24, 1f);
            setPlay();
        }
    }

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

                if (list.size() > 0)
                    if (position > 0) position--;
                    else position = list.size() - 1;

                musicService.create(position);
                musicService.start();
            }
            musicService.showNotification(R.drawable.ic_play_24, 1f);
            setPlay();
        }
    }

    @Override
    public void removeClicked() {
        try {
            musicService.stop();
            musicService.release();
            requireActivity().unbindService(this);
            requireActivity().getSupportFragmentManager().beginTransaction().detach(this).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}