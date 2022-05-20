package com.brizzs.a1musicplayer.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.brizzs.a1musicplayer.dao.PlayListDao;
import com.brizzs.a1musicplayer.model.PlayList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {PlayList.class}, version = 1, exportSchema = false)
public abstract class PlayListDB extends RoomDatabase {

    private static final int NO_OF_THREADS = 4;
    private static volatile PlayListDB instance;

    public abstract PlayListDao playListDao();

    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NO_OF_THREADS);

    public static synchronized PlayListDB getDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    PlayListDB.class, "playlist_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}
