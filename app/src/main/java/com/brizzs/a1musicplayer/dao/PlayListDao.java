package com.brizzs.a1musicplayer.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.brizzs.a1musicplayer.model.PlayList;

import java.util.List;

@Dao
public interface PlayListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PlayList playList);

    @Query("SELECT * FROM playlist")
    LiveData<List<PlayList>> getPlaylists();

}
