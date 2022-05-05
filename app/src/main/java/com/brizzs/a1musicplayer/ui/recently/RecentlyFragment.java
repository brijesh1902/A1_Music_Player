package com.brizzs.a1musicplayer.ui.recently;

import static com.brizzs.a1musicplayer.utils.Common.current_list;
import static com.brizzs.a1musicplayer.utils.Common.duration;
import static com.brizzs.a1musicplayer.utils.Common.recently;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

public class RecentlyFragment extends Fragment implements OnSongAdapterCallback {

    FragmentRecentlyBinding binding;
    View view;

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    SongsAdapter adapter;
    List<Songs> list = new ArrayList<>();
    MainViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRecentlyBinding.inflate(getLayoutInflater());
        view = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        binding.rvSongs.setHasFixedSize(true);
        binding.rvSongs.setLayoutManager(layoutManager);

        viewModel.getLiveData().observe(getViewLifecycleOwner(), songs -> {
            list = songs;
            adapter = new SongsAdapter( this, list, recently);
            binding.rvSongs.setAdapter(adapter);

            Collections.sort(list, (s1, s2) -> s2.getDate().compareTo(s1.getDate()));
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mBundleRecyclerViewState = new Bundle();
        Parcelable state = binding.rvSongs.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, state);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mBundleRecyclerViewState != null) {
            Parcelable state = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            binding.rvSongs.getLayoutManager().onRestoreInstanceState(state);
        }
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
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                .makeSceneTransitionAnimation(getActivity(),  pair1, pair2, pair3);

        startActivity(intent, optionsCompat.toBundle());
    }

    @Override
    public void AlbumCallback(int adapterPosition, List<Album> data, ImageView image, TextView name, TextView singer) {

    }
}