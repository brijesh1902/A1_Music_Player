package com.brizzs.a1musicplayer.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.brizzs.a1musicplayer.dao.AlbumDao;
import com.brizzs.a1musicplayer.model.Album;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Album.class}, version = 1, exportSchema = false)
public abstract class AlbumDB extends RoomDatabase {

    private static AlbumDB instance;
    public abstract AlbumDao albumDao();

    private static final int NO_OF_THREADS = 10;

    public static ExecutorService albumWriteExecutor = Executors.newFixedThreadPool(NO_OF_THREADS);

    public static synchronized AlbumDB getInstance(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AlbumDB.class, "album_db")
                    .fallbackToDestructiveMigration()
                    .build();

        return instance;
    }

}
