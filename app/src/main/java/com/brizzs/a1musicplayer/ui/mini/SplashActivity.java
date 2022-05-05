package com.brizzs.a1musicplayer.ui.mini;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.brizzs.a1musicplayer.BuildConfig;
import com.brizzs.a1musicplayer.R;
import com.brizzs.a1musicplayer.databinding.ActivitySplashBinding;
import com.brizzs.a1musicplayer.ui.main.MainActivity;

import org.jsoup.Jsoup;
import java.io.IOException;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;
    String newVersion, currentVersion;
    Animation animation;
    final String appPackageName = BuildConfig.APPLICATION_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_item);
        currentVersion = BuildConfig.VERSION_NAME;

        new GetVersionCode().execute();

    }

    private void openUpdateView() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashActivity.this);
        View view = getLayoutInflater().inflate(R.layout.view_update, null);
        alertDialogBuilder.setView(view);
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        TextView update = view.findViewById(R.id.update);
        TextView later = view.findViewById(R.id.later);

        update.setOnClickListener(v -> {
            v.startAnimation(animation);
            dialog.dismiss();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            }
            System.exit(0);
        });

        later.setOnClickListener(v -> {
            v.startAnimation(animation);
            dialog.dismiss();
            new Handler().postDelayed(() -> {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }, 1000);
        });
    }

    private class GetVersionCode extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + appPackageName
                        + "&hl=en")
                        .timeout(3000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select(".hAyfc .htlgb")
                        .get(7)
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return newVersion;
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                if (checkForUpdate(currentVersion, onlineVersion)) {
                    openUpdateView();
                } else {
                    new Handler().postDelayed(() -> {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }, 1000);
                }
            } else {
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }, 1000);
            }
        }
    }

    public static boolean checkForUpdate(String existingVersion, String newVersion) {

        if (existingVersion.isEmpty() || newVersion.isEmpty()) {
            return false;
        } else {
            existingVersion = existingVersion.replaceAll("\\.", "");
            newVersion = newVersion.replaceAll("\\.", "");

            int existingVersionLength = existingVersion.length();
            int newVersionLength = newVersion.length();

            StringBuilder versionBuilder = new StringBuilder();
            if (newVersionLength > existingVersionLength) {
                versionBuilder.append(existingVersion);
                for (int i = existingVersionLength; i < newVersionLength; i++) {
                    versionBuilder.append("0");
                }
                existingVersion = versionBuilder.toString();
            } else if (existingVersionLength > newVersionLength) {
                versionBuilder.append(newVersion);
                for (int i = newVersionLength; i < existingVersionLength; i++) {
                    versionBuilder.append("0");
                }
                newVersion = versionBuilder.toString();
            }
        }
        return Integer.parseInt(newVersion) > Integer.parseInt(existingVersion);
    }
}