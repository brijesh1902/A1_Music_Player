package com.brizzs.a1musicplayer.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.brizzs.a1musicplayer.model.Artist

@Dao
interface ArtistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun Insert(artist: Artist)

    @Update
    fun update(artist: Artist)

    @get:Query("SELECT * FROM artist_table")
    val allArtists: LiveData<List<Artist>>

    @Query("DELETE FROM artist_table")
    fun deleteAll()
}
