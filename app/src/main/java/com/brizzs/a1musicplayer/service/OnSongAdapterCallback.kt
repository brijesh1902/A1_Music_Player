package com.brizzs.a1musicplayer.service

import android.widget.ImageView
import android.widget.TextView
import com.brizzs.a1musicplayer.model.Album
import com.brizzs.a1musicplayer.model.Songs

interface OnSongAdapterCallback {
    fun Callback(adapterPosition: Int, data: List<Songs>, image: ImageView, name: TextView, singer: TextView)
    fun AlbumCallback(adapterPosition: Int, data: List<Album>, image: ImageView, name: TextView, singer: TextView)
}
