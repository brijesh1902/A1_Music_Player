package com.brizzs.a1musicplayer.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.brizzs.a1musicplayer.model.PlayList;
import com.brizzs.a1musicplayer.model.Songs;

import java.util.List;

@Dao
public interface SongsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Songs songs);

    @Query("SELECT * FROM songs")
    LiveData<List<Songs>> getSongs();

    @Delete
    void delete(Songs songs);

}
