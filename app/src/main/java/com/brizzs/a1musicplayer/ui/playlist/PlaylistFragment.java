package com.brizzs.a1musicplayer.ui.playlist;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.brizzs.a1musicplayer.R;
import com.brizzs.a1musicplayer.adapters.PlayListAdapter;
import com.brizzs.a1musicplayer.databinding.FragmentPlaylistBinding;

public class PlaylistFragment extends Fragment {

    FragmentPlaylistBinding binding;
    View view;
    Animation animation;
    int currentScrollPosition = 0;
    PlayListAdapter adapter;
    PlaylistViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
    public void onResume() {
        super.onResume();
        viewModel.getPlaylists().observe(getViewLifecycleOwner(), playLists -> {
            Log.e("onCreateView: ", playLists+"");
            if (playLists.size() != 0) {
                adapter = new PlayListAdapter();
                adapter.submitList(playLists);
                binding.rvSongs.setAdapter(adapter);
            }
        });

    }
}