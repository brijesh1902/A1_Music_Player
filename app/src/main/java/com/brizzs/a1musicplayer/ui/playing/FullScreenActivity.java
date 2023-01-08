package com.brizzs.a1musicplayer.ui.playing;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.brizzs.a1musicplayer.databinding.ActivityFullScreenBinding;

public class FullScreenActivity extends AppCompatActivity {

    ActivityFullScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        showWhenLockedAndTurnScreenOn();
        super.onCreate(savedInstanceState);
        binding = ActivityFullScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void showWhenLockedAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            );
        }
    }

}