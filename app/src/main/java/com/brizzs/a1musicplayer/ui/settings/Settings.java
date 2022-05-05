package com.brizzs.a1musicplayer.ui.settings;

import static com.brizzs.a1musicplayer.utils.Common.appInstalledorNot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.brizzs.a1musicplayer.BuildConfig;
import com.brizzs.a1musicplayer.R;
import com.brizzs.a1musicplayer.databinding.ActivitySettingsBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class Settings extends AppCompatActivity {

    ActivitySettingsBinding binding;
    String email = "palbrijesh59@gmail.com", linkedin = "https://www.linkedin.com/in/brijesh-pal-212956202/";
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_item);

        binding.version.setText("Version: "+ BuildConfig.VERSION_NAME);

        final String appPackageName = getApplicationContext().getPackageName();

        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);

        binding.back.setOnClickListener(v -> {
            v.startAnimation(animation);
            finish();
        });

        binding.share.setOnClickListener(v -> {
            v.startAnimation(animation);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey... I would like to invite you to "+
                     R.string.app_name+". It's really awesome platform for offline music.\n\nJoin in now with this link: https://play.google.com/store/apps/details?id=" + appPackageName);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        });

        binding.gmail.setOnClickListener(v -> {
            v.startAnimation(animation);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + email));
            startActivity(intent);
        });

        binding.linkedin.setOnClickListener(v -> {
            v.startAnimation(animation);
            try{
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(linkedin)));
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Please try again later!", Toast.LENGTH_LONG).show();
            }
        });

        binding.whatsapp.setOnClickListener(v -> {
            v.startAnimation(animation);
            boolean installed = appInstalledorNot("com.whatsapp", getApplicationContext());
            String num = "+917666898936";
            if (installed) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + num ));
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "WhatsApp is not installed in your device! Please install WhatsApp and then Contact Us!", Toast.LENGTH_LONG).show();
            }
        });



    }
}