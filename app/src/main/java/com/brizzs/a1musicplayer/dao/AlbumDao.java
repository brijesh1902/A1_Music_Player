package com.brizzs.a1musicplayer.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.brizzs.a1musicplayer.model.Album;

import java.util.List;

@Dao
public interface AlbumDao {

    @Insert
    void insert(Album album);

    @Update
    void update(Album album);

    @Query("SELECT * FROM album_table")
    LiveData<List<Album>> getAllAlbum();

}
