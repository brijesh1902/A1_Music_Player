package com.brizzs.a1musicplayer.ui.artist

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.brizzs.a1musicplayer.model.Songs
import com.brizzs.a1musicplayer.utils.Common
import java.util.ArrayList

class ArtistRepo(application: Application) {
    private val context: Context = application
    private var title: String? = null
    private var artist: String? = null
    private var data: String? = null
    private var date: String? = null
    private var duration: String? = null
    private var id: String? = null
    private var album: String? = null
    private var key: String? = null
    private val list = ArrayList<Songs>()
    private val liveData = MutableLiveData<List<Songs>>()

    @SuppressLint("Range")
    fun getArtistSongs(k: String): LiveData<List<Songs>> {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val sortRecently = MediaStore.Audio.Media.DATE_ADDED + ">" + System.currentTimeMillis() / 1000

        val cursor = context.contentResolver.query(uri, null, selection, null, sortRecently)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                date = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED))
                duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                key = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY))

                val uriImage = ContentUris.withAppendedId(Common.art_Uri, id!!.toLong())

                if (data != null && data!!.endsWith(".mp3")) {
                    if (key == k) {
                        val songs = Songs(
                            name = title ?: "",
                            artist = artist,
                            image = uriImage.toString(),
                            duration = duration,
                            data = data,
                            date = date,
                            album = album,
                            albumKey = key
                        )
                        list.add(songs)
                    }
                    liveData.postValue(list)
                }
            } while (cursor.moveToNext())
        }
        cursor?.close()

        return liveData
    }
}
