package com.brizzs.a1musicplayer.ui.artist;

import static com.brizzs.a1musicplayer.utils.Common.SPAN_COUNT;
import static com.brizzs.a1musicplayer.utils.Common.album;
import static com.brizzs.a1musicplayer.utils.Common.artist;
import static com.brizzs.a1musicplayer.utils.Common.artists;
import static com.brizzs.a1musicplayer.utils.Common.current_album;
import static com.brizzs.a1musicplayer.utils.Const.UPDATEVIEW;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.brizzs.a1musicplayer.R;
import com.brizzs.a1musicplayer.adapters.SongsAdapter;
import com.brizzs.a1musicplayer.databinding.FragmentArtistBinding;
import com.brizzs.a1musicplayer.model.Album;
import com.brizzs.a1musicplayer.model.Artist;
import com.brizzs.a1musicplayer.model.Songs;
import com.brizzs.a1musicplayer.service.OnSongAdapterCallback;
import com.brizzs.a1musicplayer.ui.album.AlbumActivity;
import com.brizzs.a1musicplayer.ui.main.MainViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ArtistFragment extends Fragment implements OnSongAdapterCallback {

    FragmentArtistBinding binding;
    View view;

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    SongsAdapter adapter;
    List<Artist> list = new ArrayList<>();
    MainViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentArtistBinding.inflate(getLayoutInflater());
        view = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), SPAN_COUNT);
        binding.rvSongs.setHasFixedSize(true);
        binding.rvSongs.setLayoutManager(layoutManager);

        viewModel.getArtistLiveData().observe(getViewLifecycleOwner(), artist -> {
            list = artist;
            adapter = new SongsAdapter( this, null, artists, layoutManager);
            adapter.setArtistList(list);
            binding.rvSongs.setAdapter(adapter);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Collections.sort(list, Comparator.comparing(Artist::getArtist));
            }

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

        UPDATEVIEW(binding.rvSongs, adapter);

    }

    @Override
    public void Callback(int adapterPosition, List<Songs> data, ImageView image, TextView name, TextView singer) {

    }

    @Override
    public void AlbumCallback(int adapterPosition, List<Album> data, ImageView image, TextView name, TextView singer) {
        /*Intent intent = new Intent(getContext(), AlbumActivity.class);
        intent.putExtra("pos", adapterPosition);
        intent.putExtra(current_album, (Serializable) data.get(adapterPosition));

        Pair<View, String> pair1 = Pair.create(image, "image");
        Pair<View, String> pair2 = Pair.create(name, "songname");
        Pair<View, String> pair3 = Pair.create(singer, "singer");
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                .makeSceneTransitionAnimation(getActivity(),  pair1, pair2, pair3);

        startActivity(intent, optionsCompat.toBundle());*/
    }

}