package com.brizzs.a1musicplayer.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.brizzs.a1musicplayer.model.Artist;

import java.util.List;

@Dao
public interface ArtistDao {

    @Insert
    void Insert(Artist artist);

    @Update
    void update(Artist artist);

    @Query("SELECT * FROM artist_table")
    LiveData<List<Artist>> getAllArtists();

    @Query("DELETE FROM artist_table")
    void deleteAll();

}
