package com.brizzs.a1musicplayer.ui.main;

import static com.brizzs.a1musicplayer.utils.Common.MUSIC_FILE;
import static com.brizzs.a1musicplayer.utils.Common.MUSIC_PLAYED;
import static com.brizzs.a1musicplayer.utils.Common.SHOW_MINI_PLAYER;
import static com.brizzs.a1musicplayer.utils.Common.current_list;
import static com.brizzs.a1musicplayer.utils.Common.duration;
import static com.brizzs.a1musicplayer.utils.Common.isUpdated;
import static com.brizzs.a1musicplayer.utils.Common.recently;
import static com.brizzs.a1musicplayer.utils.Common.sendNotification;
import static com.brizzs.a1musicplayer.utils.Common.value;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brizzs.a1musicplayer.BuildConfig;
import com.brizzs.a1musicplayer.R;
import com.brizzs.a1musicplayer.adapters.SongsAdapter;
import com.brizzs.a1musicplayer.databinding.ActivityMainBinding;
import com.brizzs.a1musicplayer.model.Album;
import com.brizzs.a1musicplayer.model.Songs;
import com.brizzs.a1musicplayer.service.OnSongAdapterCallback;
import com.brizzs.a1musicplayer.ui.artist.ArtistFragment;
import com.brizzs.a1musicplayer.ui.playing.PlayActivity;
import com.brizzs.a1musicplayer.ui.playlist.PlaylistFragment;
import com.brizzs.a1musicplayer.ui.recently.RecentlyFragment;
import com.brizzs.a1musicplayer.ui.album.AlbumFragment;
import com.brizzs.a1musicplayer.ui.settings.Settings;
import com.brizzs.a1musicplayer.utils.TinyDB;

import org.jsoup.Jsoup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnSongAdapterCallback {

    private final String KEY_RECYCLER_STATE = "recycler_state";
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private static final String TAG = "MainActivity";
    private static Bundle mBundleRecyclerViewState;
    SongsAdapter adapter;
    List<Songs> list = new ArrayList<>();
    ActivityMainBinding binding;
    MainViewModel viewModel;
    TinyDB tinyDB;
    SharedPreferences preferences;

    FragmentManager fragmentManager;
    AlbumFragment albumFragment;
    RecentlyFragment mainFragments;
    ArtistFragment artistFragment;
    PlaylistFragment playlistFragment;
    Animation animation;
    String newVersion, currentVersion = BuildConfig.VERSION_NAME;
    String appPackageName = BuildConfig.APPLICATION_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        fragmentManager = getSupportFragmentManager();
        setContentView(binding.getRoot());

        checkPermissions();

        new GetVersionCode().execute();

        tinyDB = new TinyDB(getApplicationContext());
        preferences = getSharedPreferences(MUSIC_PLAYED, MODE_PRIVATE);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_item);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.rvSongs.setHasFixedSize(true);
        binding.rvSongs.setLayoutManager(layoutManager);

        binding.searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    binding.rvSongs.setVisibility(View.VISIBLE);
                    binding.container.setVisibility(View.GONE);
                    binding.chipGroupMain.setVisibility(View.GONE);
                    searchSongs(s.toString());
                } else {
                    binding.container.setVisibility(View.VISIBLE);
                    binding.chipGroupMain.setVisibility(View.VISIBLE);
                    binding.rvSongs.setVisibility(View.GONE);
                }
            }
        });

        binding.searchClose.setOnClickListener(v -> {
            binding.searchbar.setText("");
            binding.rvSongs.setVisibility(View.GONE);
            binding.container.setVisibility(View.VISIBLE);
            binding.chipGroupMain.setVisibility(View.VISIBLE);
        });

        binding.setting.setOnClickListener(v -> {
            v.startAnimation(animation);
            startActivity(new Intent(getApplicationContext(), Settings.class));
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        });

        binding.recentlyChip.setOnClickListener(v -> {
            mainFragments = new RecentlyFragment();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_out,  // enter
                            R.anim.fade_in,    // exit
                            R.anim.fade_out,   // popEnter
                            R.anim.slide_in    // popExit
                    ).replace(R.id.container, mainFragments).commit();
            binding.recentlyChip.setChipBackgroundColorResource(R.color.lg_grey);
            binding.albumChip.setChipBackgroundColorResource(R.color.lg_blue);
            binding.artistChip.setChipBackgroundColorResource(R.color.lg_blue);
            binding.playlistChip.setChipBackgroundColorResource(R.color.lg_blue);
        });

        binding.albumChip.setOnClickListener(v -> {
            albumFragment = new AlbumFragment();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in,  // enter
                            R.anim.fade_out,  // exit
                            R.anim.fade_in,   // popEnter
                            R.anim.slide_out  // popExit
                    ).replace(R.id.container, albumFragment).commit();
            binding.albumChip.setChipBackgroundColorResource(R.color.lg_grey);
            binding.recentlyChip.setChipBackgroundColorResource(R.color.lg_blue);
            binding.artistChip.setChipBackgroundColorResource(R.color.lg_blue);
            binding.playlistChip.setChipBackgroundColorResource(R.color.lg_blue);
        });

        binding.artistChip.setOnClickListener(v -> {
            artistFragment = new ArtistFragment();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in,  // enter
                            R.anim.fade_out,  // exit
                            R.anim.fade_in,   // popEnter
                            R.anim.slide_out  // popExit
                    ).replace(R.id.container, artistFragment).commit();
            binding.artistChip.setChipBackgroundColorResource(R.color.lg_grey);
            binding.recentlyChip.setChipBackgroundColorResource(R.color.lg_blue);
            binding.albumChip.setChipBackgroundColorResource(R.color.lg_blue);
            binding.playlistChip.setChipBackgroundColorResource(R.color.lg_blue);
        });

        binding.playlistChip.setOnClickListener(v -> {
            playlistFragment = new PlaylistFragment();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in,  // enter
                            R.anim.fade_out,  // exit
                            R.anim.fade_in,   // popEnter
                            R.anim.slide_out  // popExit
                    ).replace(R.id.container, playlistFragment).commit();
            binding.playlistChip.setChipBackgroundColorResource(R.color.lg_grey);
            binding.recentlyChip.setChipBackgroundColorResource(R.color.lg_blue);
            binding.albumChip.setChipBackgroundColorResource(R.color.lg_blue);
            binding.artistChip.setChipBackgroundColorResource(R.color.lg_blue);
        });

        viewModel.getLiveData().observe(MainActivity.this, songs -> {
                list = songs;
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        mBundleRecyclerViewState = new Bundle();
        Parcelable state = Objects.requireNonNull(binding.rvSongs.getLayoutManager()).onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, state);
    }

    @Override
    protected void onResume() {
        super.onResume();

        value = preferences.getString(MUSIC_FILE, null);
        SHOW_MINI_PLAYER = value != null;

        if (mBundleRecyclerViewState != null) {
            Parcelable state = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            Objects.requireNonNull(binding.rvSongs.getLayoutManager()).onRestoreInstanceState(state);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readFile();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void checkPermissions() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            readFile();
        }

    }

    private void readFile() {
        mainFragments = new RecentlyFragment();
        fragmentManager.beginTransaction().add(R.id.container, mainFragments).commit();
        binding.recentlyChip.setChipBackgroundColorResource(R.color.lg_grey);
    }

    private void searchSongs(String s) {
        ArrayList<Songs> songslist = new ArrayList<>();
        if (list != null) {
            for (Songs songs : list) {
                if (songs.getName().toLowerCase().contains(s) || songs.getArtist().toLowerCase().contains(s) ||
                        songs.getData().toLowerCase().contains(s) || songs.getAlbum().toLowerCase().contains(s)) {
                    songslist.add(songs);
                }
            }
            adapter = new SongsAdapter(this, songslist, recently);
            binding.rvSongs.setAdapter(adapter);
        } else {
            Toast.makeText(getApplicationContext(), "Searched song not found.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void openUpdateView() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.view_update, null);
        alertDialogBuilder.setView(view);
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView update = view.findViewById(R.id.update);
        TextView later = view.findViewById(R.id.later);

        update.setOnClickListener(v -> {
            v.startAnimation(animation);
            dialog.dismiss();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            }
            System.exit(0);
        });

        later.setOnClickListener(v -> {
            v.startAnimation(animation);
            dialog.dismiss();
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
            } catch (Exception e) {
                Log.e("doInBackground: ", e.getMessage());
            }

            return newVersion;
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
//            Log.e( "onPostExecute: ", currentVersion+"----"+onlineVersion+"---"+appPackageName);
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                if (checkForUpdate(currentVersion, onlineVersion) && !isUpdated) {
                    openUpdateView();
                    sendNotification(getApplicationContext());
                    isUpdated = true;
                }
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

    @Override
    public void Callback(int adapterPosition, List<Songs> data, ImageView image, TextView name, TextView singer) {
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("pos", adapterPosition);
        intent.putExtra(duration, "0");
        intent.putExtra(current_list, (Serializable) data);

        Pair<View, String> pair1 = Pair.create(image, "image");
        Pair<View, String> pair2 = Pair.create(name, "songname");
        Pair<View, String> pair3 = Pair.create(singer, "singer");
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, pair1, pair2, pair3);

        startActivity(intent, optionsCompat.toBundle());

    }

    @Override
    public void AlbumCallback(int adapterPosition, List<Album> data, ImageView image, TextView name, TextView singer) {

    }


}