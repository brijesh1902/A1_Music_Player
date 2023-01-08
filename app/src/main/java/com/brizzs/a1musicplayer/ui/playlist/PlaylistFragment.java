package com.brizzs.a1musicplayer.ui.playlist;

import static com.brizzs.a1musicplayer.utils.Common.SPAN_COUNT;
import static com.brizzs.a1musicplayer.utils.Common.current_list;
import static com.brizzs.a1musicplayer.utils.Common.duration;
import static com.brizzs.a1musicplayer.utils.Common.playlist;
import static com.brizzs.a1musicplayer.utils.Common.recently;
import static com.brizzs.a1musicplayer.utils.Const.SONG_ARTIST;
import static com.brizzs.a1musicplayer.utils.Const.SONG_IMAGE;
import static com.brizzs.a1musicplayer.utils.Const.SONG_NAME;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brizzs.a1musicplayer.R;
import com.brizzs.a1musicplayer.adapters.PlayListAdapter;
import com.brizzs.a1musicplayer.adapters.SongsAdapter;
import com.brizzs.a1musicplayer.databinding.FragmentPlaylistBinding;
import com.brizzs.a1musicplayer.model.Album;
import com.brizzs.a1musicplayer.model.Songs;
import com.brizzs.a1musicplayer.service.OnSongAdapterCallback;
import com.brizzs.a1musicplayer.ui.playing.PlayActivity;
import com.brizzs.a1musicplayer.utils.ItemMoveCallback;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PlaylistFragment extends Fragment implements OnSongAdapterCallback {

    FragmentPlaylistBinding binding;
    View view;
    Animation animation;
    int currentScrollPosition = 0;
    PlaylistViewModel viewModel;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    SongsAdapter adapter;
    GridLayoutManager layoutManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPlaylistBinding.inflate(getLayoutInflater());
        view = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(PlaylistViewModel.class);
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_item);

        binding.create.setOnClickListener(v -> {
            v.startAnimation(animation);

        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mBundleRecyclerViewState = new Bundle();
        Parcelable state = Objects.requireNonNull(binding.rvSongs.getLayoutManager()).onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, state);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();

        layoutManager = new GridLayoutManager(getContext(), SPAN_COUNT);
        binding.rvSongs.setHasFixedSize(true);
        binding.rvSongs.setLayoutManager(layoutManager);
        binding.rvSongs.setItemAnimator(null);

        viewModel.getPlaylists().observe(getViewLifecycleOwner(), songs -> {
            if (songs.size() > 0) {
                adapter = new SongsAdapter(this, songs, playlist, layoutManager);
                binding.rvSongs.setAdapter(adapter);
                binding.rvSongs.post(() -> adapter.notifyDataSetChanged());

                Collections.sort(songs, (s1, s2) -> s2.getDate().compareTo(s1.getDate()));
            } else {
                Toast.makeText(requireActivity(), "No songs are added to favourite list.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.rvSongs.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                currentScrollPosition += dy;
                if (currentScrollPosition > 0) {
                    binding.top.setVisibility(View.VISIBLE);
                } else binding.top.setVisibility(View.GONE);
            }
        });

        binding.top.setOnClickListener(v -> {
            v.startAnimation(animation);
            currentScrollPosition = 0;
            binding.rvSongs.smoothScrollToPosition(0);
        });

        if (mBundleRecyclerViewState != null) {
            Parcelable state = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            Objects.requireNonNull(binding.rvSongs.getLayoutManager()).onRestoreInstanceState(state);
        }

        if (currentScrollPosition > 0) {
            binding.top.setVisibility(View.VISIBLE);
        } else binding.top.setVisibility(View.GONE);

    }

    @Override
    public void Callback(int adapterPosition, List<Songs> data, ImageView image, TextView name, TextView singer) {
        Intent intent = new Intent(getContext(), PlayActivity.class);
        intent.putExtra("pos", adapterPosition);
        intent.putExtra(duration, "0");
        intent.putExtra(current_list, (Serializable) data);

        Pair<View, String> pair1 = Pair.create(image, SONG_IMAGE);
        Pair<View, String> pair2 = Pair.create(name, SONG_NAME);
        Pair<View, String> pair3 = Pair.create(singer, SONG_ARTIST);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(),  pair1, pair2, pair3);

        startActivity(intent, optionsCompat.toBundle());
    }

    @Override
    public void AlbumCallback(int adapterPosition, List<Album> data, ImageView image, TextView name, TextView singer) {

    }
}