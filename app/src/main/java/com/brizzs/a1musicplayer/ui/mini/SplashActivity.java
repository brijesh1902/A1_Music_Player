package com.brizzs.a1musicplayer.ui.mini;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.brizzs.a1musicplayer.databinding.ActivitySplashBinding;
import com.brizzs.a1musicplayer.ui.main.MainActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        startActivity();

    }

    @Override
    protected void onResume() {
        super.onResume();

        binding.card.post(() -> binding.card.performClick());

    }

    private void startActivity() {

        new Handler().postDelayed(() -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }, 1200);
    }

}