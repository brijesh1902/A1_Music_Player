package com.brizzs.a1musicplayer.service;

import android.widget.ImageView;
import android.widget.TextView;

import com.brizzs.a1musicplayer.model.Album;
import com.brizzs.a1musicplayer.model.Songs;

import java.util.List;

public interface OnSongAdapterCallback {

    void Callback(int adapterPosition, List<Songs> data, ImageView image, TextView name, TextView singer);

    void AlbumCallback(int adapterPosition, List<Album> data, ImageView image, TextView name, TextView singer);

}
