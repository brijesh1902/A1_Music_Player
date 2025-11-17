package com.brizzs.a1musicplayer.ui.playing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
        // Look for and REMOVE or comment out this block:
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            // This code typically applies padding equal to the system bar height
            // to prevent content from going behind the bars.
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Removing the logic that sets padding here will also help disable the effect.
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });
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