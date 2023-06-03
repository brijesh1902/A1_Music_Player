package com.brizzs.a1musicplayer.ui.recently;

import static com.brizzs.a1musicplayer.ui.playing.PlayActivity.position;
import static com.brizzs.a1musicplayer.utils.Common.ISGRIDVIEW;
import static com.brizzs.a1musicplayer.utils.Common.SPAN_COUNT;
import static com.brizzs.a1musicplayer.utils.Common.current_list;
import static com.brizzs.a1musicplayer.utils.Common.current_position;
import static com.brizzs.a1musicplayer.utils.Common.duration;
import static com.brizzs.a1musicplayer.utils.Common.recently;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.brizzs.a1musicplayer.adapters.SongsAdapter;
import com.brizzs.a1musicplayer.databinding.FragmentRecentlyBinding;
import com.brizzs.a1musicplayer.model.Album;
import com.brizzs.a1musicplayer.model.Songs;
import com.brizzs.a1musicplayer.service.OnSongAdapterCallback;
import com.brizzs.a1musicplayer.ui.playing.PlayActivity;
import com.brizzs.a1musicplayer.ui.main.MainViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class RecentlyFragment extends Fragment implements OnSongAdapterCallback {

    FragmentRecentlyBinding binding;
    View view;

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    SongsAdapter adapter;
    MainViewModel viewModel;
    Animation animation;
    int currentScrollPosition = 0;
    GridLayoutManager layoutManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRecentlyBinding.inflate(getLayoutInflater());
        view = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_item);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mBundleRecyclerViewState = new Bundle();
        Parcelable state = Objects.requireNonNull(binding.rvSongs.getLayoutManager()).onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, state);
    }

    @Override
    public void onResume() {
        super.onResume();

        layoutManager = new GridLayoutManager(getContext(), SPAN_COUNT);
        binding.rvSongs.setHasFixedSize(true);
        binding.rvSongs.setLayoutManager(layoutManager);
        binding.rvSongs.setItemAnimator(null);

        viewModel.getLiveData().observe(getViewLifecycleOwner(), songs -> {
            adapter = new SongsAdapter( this, songs, recently, layoutManager);
            binding.rvSongs.setAdapter(adapter);

            Collections.sort(songs, (s1, s2) -> s2.getDate().compareTo(s1.getDate()));
        });
        binding.rvSongs.smoothScrollToPosition(0);

        binding.rvSongs.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                currentScrollPosition += dy;
                if( currentScrollPosition > 0 ) {
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

        if(currentScrollPosition > 0 ) {
            binding.top.setVisibility(View.VISIBLE);
        } else binding.top.setVisibility(View.GONE);

    }

    public void refresh(){
        requireActivity().onDetachedFromWindow();
        Toast.makeText(requireContext(), ISGRIDVIEW+"", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void Callback(int adapterPosition, List<Songs> data, ImageView image, TextView name, TextView singer) {
        Intent intent = new Intent(getContext(), PlayActivity.class);
        intent.putExtra("pos", adapterPosition);
        intent.putExtra(duration, "0");
        intent.putExtra(current_list, (Serializable) data);

        Pair<View, String> pair1 = Pair.create(image, "image");
        Pair<View, String> pair2 = Pair.create(name, "songname");
        Pair<View, String> pair3 = Pair.create(singer, "singer");
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(),  pair1, pair2, pair3);

        startActivity(intent, optionsCompat.toBundle());
    }

    @Override
    public void AlbumCallback(int adapterPosition, List<Album> data, ImageView image, TextView name, TextView singer) {

    }
}