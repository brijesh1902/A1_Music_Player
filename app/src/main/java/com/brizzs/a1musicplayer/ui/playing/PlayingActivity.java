package com.brizzs.a1musicplayer.ui.playing;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.brizzs.a1musicplayer.databinding.ActivityPlayingBinding;
import com.google.android.exoplayer2.ExoPlayer;

public class PlayingActivity extends AppCompatActivity {

    ActivityPlayingBinding binding;
    ExoPlayer exoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}