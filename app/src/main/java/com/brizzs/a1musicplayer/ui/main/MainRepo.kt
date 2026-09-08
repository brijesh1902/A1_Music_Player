package com.brizzs.a1musicplayer.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.brizzs.a1musicplayer.dao.AlbumDao
import com.brizzs.a1musicplayer.dao.ArtistDao
import com.brizzs.a1musicplayer.dao.PlayListDao
import com.brizzs.a1musicplayer.db.AlbumDB
import com.brizzs.a1musicplayer.db.ArtistDB
import com.brizzs.a1musicplayer.db.PlayListDB
import com.brizzs.a1musicplayer.model.Album
import com.brizzs.a1musicplayer.model.Artist
import com.brizzs.a1musicplayer.model.Songs
import com.brizzs.a1musicplayer.utils.Common
import com.brizzs.a1musicplayer.utils.MyApplication
import java.util.ArrayList

class MainRepo(application: Application) {
    private val albumDao: AlbumDao = MyApplication.getInstance().getAlbumDao()
    private val artistDao: ArtistDao = MyApplication.getInstance().getArtistDao()
    private val playListDao: PlayListDao = MyApplication.getInstance().getPlayListDao()
    private val albumliveData: LiveData<List<Album>> = albumDao.allAlbum
    private val artistliveData: LiveData<List<Artist>> = artistDao.allArtists
    private val context: Context = application
    private val liveData = MutableLiveData<List<Songs>>()

    @SuppressLint("Range")
    fun getSongs(): MutableLiveData<List<Songs>> {
        val songList = ArrayList<Songs>()

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
        ) {
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
            val sortRecently = MediaStore.Audio.Media.DATE_ADDED + ">" + System.currentTimeMillis() / 1000
            val cursor = context.contentResolver.query(uri, null, selection, null, sortRecently)

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                    val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val date = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED))
                    val duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))

                    if (data != null && data.endsWith(".mp3")) {
                        val uriImage = ContentUris.withAppendedId(Common.alb_Uri, id!!.toLong())
                        val songs = Songs(
                            name = title ?: "",
                            artist = artist,
                            image = uriImage.toString(),
                            duration = duration,
                            data = data,
                            date = date,
                            album = album,
                            albumKey = id
                        )
                        songList.add(songs)
                        liveData.value = songList
                    }
                } while (cursor.moveToNext())
            } else {
                deleteAll()
            }
            cursor?.close()
        }
        return liveData
    }

    @SuppressLint("Range")
    fun getAlbumSongs(): LiveData<List<Album>> {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val sort = MediaStore.Audio.Media.ALBUM + " COLLATE NOCASE ASC"
        val cursor = context.contentResolver.query(uri, null, selection, null, sort)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val artist = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ARTIST))
                } else {
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                }
                val album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                val id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                val key = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY))

                if (data != null && data.endsWith(".mp3")) {
                    val uriImage = ContentUris.withAppendedId(Common.alb_Uri, id!!.toLong())
                    val albums = Album(
                        id = key ?: "",
                        album = album,
                        artist = artist,
                        image = uriImage.toString()
                    )
                    InsertTask(albumDao).execute(albums)
                }
            } while (cursor.moveToNext())
        } else {
            deleteAll()
        }
        cursor?.close()
        return albumliveData
    }

    @SuppressLint("Range")
    fun getArtistSongs(): LiveData<List<Artist>> {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val sort = MediaStore.Audio.Media.ALBUM + " COLLATE NOCASE ASC"
        val cursor = context.contentResolver.query(uri, null, selection, null, sort)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                val id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID))
                val key = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))

                val uriImage = ContentUris.withAppendedId(Common.art_Uri, key!!.toLong())
                if (data != null && data.endsWith(".mp3")) {
                    val art = Artist(
                        id = id ?: "",
                        songName = data,
                        artist = artist,
                        image = uriImage.toString()
                    )
                    insertArtist(art)
                }
            } while (cursor.moveToNext())
        } else {
            deleteAll()
        }
        cursor?.close()
        return artistliveData
    }

    private class InsertTask(private val dao: AlbumDao) : AsyncTask<Album, Void, Void>() {
        override fun doInBackground(vararg albums: Album): Void? {
            try {
                dao.insert(albums[0])
            } catch (e: Exception) {
                Log.i("doInBackground: ", e.toString())
            }
            return null
        }
    }

    private fun insertArtist(art: Artist) {
        ArtistDB.artistWriteExecutor.execute { artistDao.Insert(art) }
    }

    private fun deleteAll() {
        AlbumDB.albumWriteExecutor.execute { albumDao.deleteAll() }
        ArtistDB.artistWriteExecutor.execute { artistDao.deleteAll() }
        PlayListDB.playlistWriteExecutor.execute { playListDao.deleteAll() }
    }
}
