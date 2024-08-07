package com.brizzs.a1musicplayer.ui.main;

import static com.brizzs.a1musicplayer.utils.Common.ISGRIDVIEW;
import static com.brizzs.a1musicplayer.utils.Common.MUSIC_FILE;
import static com.brizzs.a1musicplayer.utils.Common.MUSIC_PLAYED;
import static com.brizzs.a1musicplayer.utils.Common.SHOW_MINI_PLAYER;
import static com.brizzs.a1musicplayer.utils.Common.SPAN_COUNT;
import static com.brizzs.a1musicplayer.utils.Common.SPAN_COUNT_ONE;
import static com.brizzs.a1musicplayer.utils.Common.current_list;
import static com.brizzs.a1musicplayer.utils.Common.duration;
import static com.brizzs.a1musicplayer.utils.Common.recently;
import static com.brizzs.a1musicplayer.utils.Common.value;
import static com.brizzs.a1musicplayer.utils.Const.CLICKANIMATION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.brizzs.a1musicplayer.BuildConfig;
import com.brizzs.a1musicplayer.R;
import com.brizzs.a1musicplayer.adapters.SongsAdapter;
import com.brizzs.a1musicplayer.databinding.ActivityMainBinding;
import com.brizzs.a1musicplayer.model.Album;
import com.brizzs.a1musicplayer.model.Songs;
import com.brizzs.a1musicplayer.service.OnSongAdapterCallback;
import com.brizzs.a1musicplayer.ui.album.AlbumFragment;
import com.brizzs.a1musicplayer.ui.artist.ArtistFragment;
import com.brizzs.a1musicplayer.ui.playing.PlayActivity;
import com.brizzs.a1musicplayer.ui.playlist.PlaylistFragment;
import com.brizzs.a1musicplayer.ui.recently.RecentlyFragment;
import com.brizzs.a1musicplayer.ui.settings.SettingsActivity;
import com.brizzs.a1musicplayer.utils.PermissionChecker;
import com.brizzs.a1musicplayer.utils.SharePreference;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnSongAdapterCallback {

    private final String KEY_RECYCLER_STATE = "recycler_state";
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 100;
    private static final String TAG = "MainActivity";
    private static Bundle mBundleRecyclerViewState;
    SongsAdapter adapter;
    List<Songs> list = new ArrayList<>();
    ActivityMainBinding binding;
    MainViewModel viewModel;
    SharePreference sharePreference;
    SharedPreferences preferences;
    GridLayoutManager gridLayoutManager;
    FragmentManager fragmentManager;
    Animation animation;
    String appPackageName = BuildConfig.APPLICATION_ID;

    private AppUpdateManager mAppUpdateManager;
    private static final int RC_APP_UPDATE = 11;
    int fragmentNo = 0;
    boolean isGranted;
    PermissionChecker permissionChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        fragmentManager = getSupportFragmentManager();
        setContentView(binding.getRoot());

        permissionChecker = new PermissionChecker(this);
        sharePreference = new SharePreference(getApplicationContext());

        preferences = getSharedPreferences(MUSIC_PLAYED, MODE_PRIVATE);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_item);

        gridLayoutManager = new GridLayoutManager(this, SPAN_COUNT_ONE);
        binding.rvSongs.setHasFixedSize(true);
        binding.rvSongs.setLayoutManager(gridLayoutManager);

        int val = sharePreference.getTimesOpen();
        sharePreference.setTimesOpen(val + 1);

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
                    binding.btnView.setVisibility(View.GONE);
                    binding.chipGroupMain.setVisibility(View.GONE);
                    searchSongs(s.toString());
                } else {
                    binding.container.setVisibility(View.VISIBLE);
                    binding.chipGroupMain.setVisibility(View.VISIBLE);
                    binding.btnView.setVisibility(View.VISIBLE);
                    binding.rvSongs.setVisibility(View.GONE);
                }
            }
        });

        binding.searchClose.setOnClickListener(v -> {
            binding.searchbar.setText("");
            binding.rvSongs.setVisibility(View.GONE);
            binding.container.setVisibility(View.VISIBLE);
            binding.chipGroupMain.setVisibility(View.VISIBLE);
            binding.btnView.setVisibility(View.VISIBLE);
        });

        binding.setting.setOnClickListener(v -> {
            v.startAnimation(animation);
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        });

        viewModel.getLiveData().observe(MainActivity.this, songs -> {
            list = songs;
        });

    }

    void reviewDialog() {
        ReviewManager manager = ReviewManagerFactory.create(MainActivity.this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(MainActivity.this, reviewInfo);
                flow.addOnCompleteListener(task1 -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown.
                });
            } else
                Log.e("reviewDialog: ", Objects.requireNonNull(task.getException()).getLocalizedMessage());
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        mBundleRecyclerViewState = new Bundle();
        Parcelable state = Objects.requireNonNull(binding.rvSongs.getLayoutManager()).onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, state);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onResume() {
        super.onResume();

        isGranted = sharePreference.isGranted();

        if (!permissionChecker.areAllPermissionsGranted()) {
            checkPermissions();
        } else {
            binding.permission.setVisibility(View.GONE);
            readFile();
        }

        binding.permission.setOnClickListener(view -> {
            permissionChecker.requestPermission();
        });

        if (sharePreference.getTimesOpen() >= 3)
            reviewDialog();

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
            Objects.requireNonNull(fragmentManager.findFragmentById(binding.container.getId())).onResume();
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
//                    openUpdateView();
                    mAppUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE /*IMMEDIATE*/, MainActivity.this, RC_APP_UPDATE);
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
        fragmentNo = 0;
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_out,  // enter
                        R.anim.fade_in,    // exit
                        R.anim.fade_out,   // popEnter
                        R.anim.slide_in    // popExit
                ).replace(R.id.container, new RecentlyFragment()).commit();
    }

    private void checkedAlbum() {
        fragmentNo = 1;
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                ).replace(R.id.container, new AlbumFragment()).commit();
    }

    private void checkedPlaylist() {
        fragmentNo = 2;
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                ).replace(R.id.container, new PlaylistFragment()).commit();
    }

    private void checkedArtist() {
        fragmentNo = 3;
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                ).replace(R.id.container, new ArtistFragment()).commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0){
            if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    binding.permission.setVisibility(View.GONE);
                    readFile();
                } else {
                    binding.permission.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void checkPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE, Manifest.permission.MEDIA_CONTENT_CONTROL}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    private void readFile() {
        fragmentManager.beginTransaction().replace(R.id.container, new RecentlyFragment()).commit();
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