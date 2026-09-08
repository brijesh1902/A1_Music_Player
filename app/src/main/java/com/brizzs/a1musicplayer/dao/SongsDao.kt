package com.brizzs.a1musicplayer.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.brizzs.a1musicplayer.model.Songs

@Dao
interface SongsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(songs: Songs)

    @get:Query("SELECT * FROM songs")
    val songs: LiveData<List<Songs>>

    @Delete
    fun delete(songs: Songs)
}
