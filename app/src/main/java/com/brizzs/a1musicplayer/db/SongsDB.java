package com.brizzs.a1musicplayer.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.brizzs.a1musicplayer.dao.SongsDao;
import com.brizzs.a1musicplayer.model.Songs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Songs.class}, version = 1, exportSchema = false)
public abstract class SongsDB extends RoomDatabase {

    public abstract SongsDao songsDao();

    private static final int NO_OF_THREADS = 4;
    private static SongsDB instance;

    public static ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NO_OF_THREADS);

    public static synchronized SongsDB getDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    SongsDB.class, "songs_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}
