package com.brizzs.a1musicplayer.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.brizzs.a1musicplayer.dao.ArtistDao;
import com.brizzs.a1musicplayer.model.Artist;

@Database(entities = {Artist.class}, version = 1, exportSchema = false)
public abstract class ArtistDB extends RoomDatabase {

    public static ArtistDB instance;
    public abstract ArtistDao artistDao();

    public static synchronized ArtistDB getInstance(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    ArtistDB.class, "artist_db")
                    .fallbackToDestructiveMigration()
                    .build();

        return instance;
    }

}
