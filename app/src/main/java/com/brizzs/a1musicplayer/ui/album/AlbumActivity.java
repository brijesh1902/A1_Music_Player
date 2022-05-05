package com.brizzs.a1musicplayer.ui.album;

import static com.brizzs.a1musicplayer.utils.Common.current_album;
import static com.brizzs.a1musicplayer.utils.Common.current_list;
import static com.brizzs.a1musicplayer.utils.Common.duration;
import static com.brizzs.a1musicplayer.utils.Common.recently;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.brizzs.a1musicplayer.R;
import com.brizzs.a1musicplayer.adapters.SongsAdapter;
import com.brizzs.a1musicplayer.databinding.ActivityAlbumBinding;
import com.brizzs.a1musicplayer.model.Album;
import com.brizzs.a1musicplayer.model.Songs;
import com.brizzs.a1musicplayer.service.OnSongAdapterCallback;
import com.brizzs.a1musicplayer.ui.playing.PlayActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AlbumActivity extends AppCompatActivity implements OnSongAdapterCallback {

    private ActivityAlbumBinding binding;
    Album currentAlbum;
    AlbumViewModel viewModel;
    SongsAdapter adapter;
    List<Songs> list = new ArrayList<>();
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAlbumBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = findViewById(R.id.rv_songs);
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;

        currentAlbum = (Album) getIntent().getSerializableExtra(current_album);

        toolBarLayout.setTitle(currentAlbum.getAlbum());
        Glide.with(getApplicationContext()).load(currentAlbum.getImage())
                .placeholder(R.drawable.music_note_24).error(R.drawable.music_note_24)
                .transition(DrawableTransitionOptions.withCrossFade()).into(binding.img);

        viewModel = new ViewModelProvider(this).get(AlbumViewModel.class);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        viewModel.getAlbumSongsLiveData(currentAlbum.getId()).observe(this, songs -> {
            list = songs;
            adapter = new SongsAdapter( this, list, recently);
            recyclerView.setAdapter(adapter);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Collections.sort(list, Comparator.comparing(Songs::getName));
            }

        });


    }

    @Override
    public void Callback(int adapterPosition, List<Songs> data, ImageView image, TextView name, TextView singer) {
        Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
        intent.putExtra("pos", adapterPosition);
        intent.putExtra(duration, "0");
        intent.putExtra(current_list, (Serializable) data);

        Pair<View, String> pair1 = Pair.create(image, "image");
        Pair<View, String> pair2 = Pair.create(name, "songname");
        Pair<View, String> pair3 = Pair.create(singer, "singer");
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this,  pair1, pair2, pair3);

        startActivity(intent, optionsCompat.toBundle());
    }

    @Override
    public void AlbumCallback(int adapterPosition, List<Album> data, ImageView image, TextView name, TextView singer) {

    }
}