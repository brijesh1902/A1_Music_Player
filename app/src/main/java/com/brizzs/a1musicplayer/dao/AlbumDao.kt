package com.brizzs.a1musicplayer.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.brizzs.a1musicplayer.model.Album

@Dao
interface AlbumDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(album: Album)

    @Update
    fun update(album: Album)

    @get:Query("SELECT * FROM album_table")
    val allAlbum: LiveData<List<Album>>

    @Query("DELETE FROM album_table")
    fun deleteAll()
}
