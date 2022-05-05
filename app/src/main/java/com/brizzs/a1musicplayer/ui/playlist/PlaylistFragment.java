package com.brizzs.a1musicplayer.ui.playlist;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brizzs.a1musicplayer.databinding.FragmentPlaylistBinding;

public class PlaylistFragment extends Fragment {

    FragmentPlaylistBinding binding;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPlaylistBinding.inflate(getLayoutInflater());
        view = binding.getRoot();



        return view;
    }
}