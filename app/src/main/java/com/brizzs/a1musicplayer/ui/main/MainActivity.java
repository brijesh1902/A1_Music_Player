package com.brizzs.a1musicplayer.ui.main;

import static com.brizzs.a1musicplayer.utils.Common.ISGRIDVIEW;
import static com.brizzs.a1musicplayer.utils.Common.SPAN_COUNT;
import static com.brizzs.a1musicplayer.utils.Common.MUSIC_FILE;
import static com.brizzs.a1musicplayer.utils.Common.MUSIC_PLAYED;
import static com.brizzs.a1musicplayer.utils.Common.SHOW_MINI_PLAYER;
import static com.brizzs.a1musicplayer.utils.Common.SPAN_COUNT_ONE;
import static com.brizzs.a1musicplayer.utils.Common.current_list;
import static com.brizzs.a1musicplayer.utils.Common.duration;
import static com.brizzs.a1musicplayer.utils.Common.isUpdated;
import static com.brizzs.a1musicplayer.utils.Common.recently;
import static com.brizzs.a1musicplayer.utils.Common.sendNotification;
import static com.brizzs.a1musicplayer.utils.Common.value;
import static com.brizzs.a1musicplayer.utils.Const.CLICKANIMATION;
import static com.brizzs.a1musicplayer.utils.Const.UPDATEVIEW;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RadioGroup;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

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
    GridLayoutManager gridLayoutManager;

    FragmentManager fragmentManager;
    AlbumFragment albumFragment;
    RecentlyFragment mainFragments;
    ArtistFragment artistFragment;
    PlaylistFragment playlistFragment;
    Animation animation;
    String newVersion, currentVersion = BuildConfig.VERSION_NAME;
    String appPackageName = BuildConfig.APPLICATION_ID;
    private BroadcastReceiver mMessageReceiver = null;

    private AppUpdateManager mAppUpdateManager;
    private static final int RC_APP_UPDATE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        fragmentManager = getSupportFragmentManager();
        setContentView(binding.getRoot());

        checkPermissions();

        tinyDB = new TinyDB(getApplicationContext());
        preferences = getSharedPreferences(MUSIC_PLAYED, MODE_PRIVATE);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_item);

        mainFragments = new RecentlyFragment();
        albumFragment = new AlbumFragment();
        playlistFragment = new PlaylistFragment();
        artistFragment = new ArtistFragment();

        gridLayoutManager = new GridLayoutManager(this, SPAN_COUNT_ONE);
        binding.rvSongs.setHasFixedSize(true);
        binding.rvSongs.setLayoutManager(gridLayoutManager);

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

        binding.chipGroupMain.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.recently_chip:
                    checkedRecently();
                    break;
                case R.id.album_chip:
                    checkedAlbum();
                    break;
                case R.id.playlist_chip:
                    checkedPlaylist();
                    break;
                case R.id.artist_chip:
                    checkedArtist();
                    break;
            }
        });

        binding.btnView.setOnClickListener(v -> {
            CLICKANIMATION(v);
            if (ISGRIDVIEW) {
                binding.btnView.setBackgroundResource(R.drawable.ic_baseline_grid_view_24);
                SPAN_COUNT = 1;
                ISGRIDVIEW = false;
            } else {
                binding.btnView.setBackgroundResource(R.drawable.ic_baseline_view_list_24);
                SPAN_COUNT = 2;
                ISGRIDVIEW = true;
            }
            Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.container)).onResume();
            binding.btnView.invalidate();
        });

       new Thread(() -> {
           if (ISGRIDVIEW) {
               binding.btnView.setBackgroundResource(R.drawable.ic_baseline_view_list_24);
           } else {
               binding.btnView.setBackgroundResource(R.drawable.ic_baseline_grid_view_24);
           }
       }).start();

        value = preferences.getString(MUSIC_FILE, null);
        SHOW_MINI_PLAYER = value != null;

        if (mBundleRecyclerViewState != null) {
            Parcelable state = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            Objects.requireNonNull(binding.rvSongs.getLayoutManager()).onRestoreInstanceState(state);
        }

        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateManager.registerListener(installStateUpdatedListener);
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE /*IMMEDIATE*/)) {
                try {
                    openUpdateView();
                    mAppUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE /*IMMEDIATE*/, (Activity) getApplicationContext(), RC_APP_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(InstallState state) {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip

            } else if (state.installStatus() == InstallStatus.INSTALLED) {
                if (mAppUpdateManager != null) {
                    mAppUpdateManager.unregisterListener(installStateUpdatedListener);
                }
            } else {
                Log.i(TAG, "InstallStateUpdatedListener: state: " + state.installStatus());
            }
        }
    };


    private void checkedRecently() {
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_out,  // enter
                        R.anim.fade_in,    // exit
                        R.anim.fade_out,   // popEnter
                        R.anim.slide_in    // popExit
                ).replace(R.id.container, mainFragments).commit();
    }

    private void checkedAlbum() {
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                ).replace(R.id.container, albumFragment).commit();
    }

    private void checkedPlaylist() {
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                ).replace(R.id.container, playlistFragment).commit();
    }

    private void checkedArtist() {
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                ).replace(R.id.container, artistFragment).commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

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
            adapter = new SongsAdapter(this, songslist, recently, gridLayoutManager);
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