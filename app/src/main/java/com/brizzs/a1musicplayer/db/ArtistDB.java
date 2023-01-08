package com.brizzs.a1musicplayer.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.brizzs.a1musicplayer.dao.ArtistDao;
import com.brizzs.a1musicplayer.model.Artist;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Artist.class}, version = 1, exportSchema = false)
public abstract class ArtistDB extends RoomDatabase {

    public static ArtistDB instance;
    public abstract ArtistDao artistDao();

    private static final int NO_OF_THREADS = 10;

    public static ExecutorService artistWriteExecutor = Executors.newFixedThreadPool(NO_OF_THREADS);

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
