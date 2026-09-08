package com.brizzs.a1musicplayer.ui.album

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.brizzs.a1musicplayer.model.Songs

class AlbumViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: AlbumRepo = AlbumRepo(application)

    fun getAlbumSongsLiveData(key: String): LiveData<List<Songs>> {
        return repo.getSongsAlbum(key)
    }
}
