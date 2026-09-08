package com.brizzs.a1musicplayer.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.brizzs.a1musicplayer.model.Album
import com.brizzs.a1musicplayer.model.Artist
import com.brizzs.a1musicplayer.model.Songs
import com.brizzs.a1musicplayer.utils.Common.SPAN_COUNT

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: MainRepo = MainRepo(application)
    var spanCount: Int = SPAN_COUNT

    val songsLiveData: LiveData<List<Songs>> by lazy { repo.getSongs() }
    val albumLiveData: LiveData<List<Album>> by lazy { repo.getAlbumSongs() }
    val artistLiveData: LiveData<List<Artist>> by lazy { repo.getArtistSongs() }

    fun getLiveData(): LiveData<List<Songs>> = songsLiveData
}
