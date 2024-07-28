package com.brizzs.a1musicplayer.ui.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.brizzs.a1musicplayer.databinding.ActivitySplashBinding;
import com.brizzs.a1musicplayer.ui.main.MainActivity;
import com.brizzs.a1musicplayer.utils.MyApplication;

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
            if (MyApplication.getSharePreference().isFirstRun()){
                startActivity(new Intent(getApplicationContext(), OnBoardingActivity.class));
            } else {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
            finish();
        }, 1200);
    }

}
