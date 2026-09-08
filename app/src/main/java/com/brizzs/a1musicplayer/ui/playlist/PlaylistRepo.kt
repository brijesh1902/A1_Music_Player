package com.brizzs.a1musicplayer.ui.playlist

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.brizzs.a1musicplayer.dao.PlayListDao
import com.brizzs.a1musicplayer.dao.SongsDao
import com.brizzs.a1musicplayer.db.PlayListDB
import com.brizzs.a1musicplayer.db.SongsDB
import com.brizzs.a1musicplayer.model.PlayList
import com.brizzs.a1musicplayer.model.Songs
import com.brizzs.a1musicplayer.utils.Common.FAVOURITES

class PlaylistRepo(application: Application) {
    private val context: Context = application
    private val playListDao: PlayListDao = PlayListDB.getInstance(application).playListDao()
    private val songsDao: SongsDao = SongsDB.getDatabase(application).songsDao()
    val liveSongs: LiveData<List<Songs>> = songsDao.songs
    var value: Int = -1

    fun setToFavourites(songs: Songs) {
        val playList = PlayList(
            id = "1",
            name = FAVOURITES,
            image1 = songs.image,
            image2 = songs.image,
            image3 = songs.image,
            image4 = songs.image
        )

        PlayListDB.playlistWriteExecutor.execute {
            playListDao.insert(playList)
        }
    }
}
