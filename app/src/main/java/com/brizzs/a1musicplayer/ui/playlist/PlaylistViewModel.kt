package com.brizzs.a1musicplayer.ui.playlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.brizzs.a1musicplayer.model.Songs

class PlaylistViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: PlaylistRepo = PlaylistRepo(application)
    var isAvailable: Boolean = repo.value == 1

    fun getPlaylists(): LiveData<List<Songs>> {
        return repo.liveSongs
    }

    fun insertToFavourites(songs: Songs) {
        repo.setToFavourites(songs)
    }
}
