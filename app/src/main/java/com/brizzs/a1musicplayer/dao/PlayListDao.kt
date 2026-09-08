package com.brizzs.a1musicplayer.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.brizzs.a1musicplayer.model.PlayList

@Dao
interface PlayListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(playList: PlayList)

    @get:Query("SELECT * FROM playlist")
    val playlists: LiveData<List<PlayList>>

    @Query("DELETE FROM playlist")
    fun deleteAll()
}
