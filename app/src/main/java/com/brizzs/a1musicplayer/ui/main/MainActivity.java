package com.brizzs.a1musicplayer.ui.main;

import static com.brizzs.a1musicplayer.ui.playing.PlayActivity.songslist;
import static com.brizzs.a1musicplayer.utils.Common.MUSIC_FILE;
import static com.brizzs.a1musicplayer.utils.Common.MUSIC_PLAYED;
import static com.brizzs.a1musicplayer.utils.Common.SHOW_MINI_PLAYER;
import static com.brizzs.a1musicplayer.utils.Common.current_list;
import static com.brizzs.a1musicplayer.utils.Common.duration;
import static com.brizzs.a1musicplayer.utils.Common.isServiceRunning;
import static com.brizzs.a1musicplayer.utils.Common.recently;
import static com.brizzs.a1musicplayer.utils.Common.value;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.brizzs.a1musicplayer.R;
import com.brizzs.a1musicplayer.adapters.SongsAdapter;
import com.brizzs.a1musicplayer.databinding.ActivityMainBinding;
import com.brizzs.a1musicplayer.model.Album;
import com.brizzs.a1musicplayer.model.Songs;
import com.brizzs.a1musicplayer.service.MusicService;
import com.brizzs.a1musicplayer.service.OnSongAdapterCallback;
import com.brizzs.a1musicplayer.ui.artist.ArtistFragment;
import com.brizzs.a1musicplayer.ui.playing.PlayActivity;
import com.brizzs.a1musicplayer.ui.playlist.PlaylistFragment;
import com.brizzs.a1musicplayer.ui.recently.RecentlyFragment;
import com.brizzs.a1musicplayer.ui.album.AlbumFragment;
import com.brizzs.a1musicplayer.ui.settings.Settings;
import com.brizzs.a1musicplayer.utils.PermissionsHandling;
import com.brizzs.a1musicplayer.utils.TinyDB;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnSongAdapterCallback {

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static final String TAG = "MAIN";
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

    private void checkPermissions() {

        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    mainFragments = new RecentlyFragment();
                    fragmentManager.beginTransaction().add(R.id.container, mainFragments).commit();
                    binding.recentlyChip.setChipBackgroundColorResource(R.color.lg_grey);
                } else {
                    checkPermissions();
                }

                if (report.isAnyPermissionPermanentlyDenied()) {
                    checkPermissions();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).onSameThread().check();

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
            Toast.makeText(getApplicationContext(), "Searched data not found.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
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
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                .makeSceneTransitionAnimation(MainActivity.this, pair1, pair2, pair3);

        startActivity(intent, optionsCompat.toBundle());

    }

    @Override
    public void AlbumCallback(int adapterPosition, List<Album> data, ImageView image, TextView name, TextView singer) {

    }
}