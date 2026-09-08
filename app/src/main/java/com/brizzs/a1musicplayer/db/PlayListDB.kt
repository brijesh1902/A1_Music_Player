package com.brizzs.a1musicplayer.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.brizzs.a1musicplayer.dao.PlayListDao
import com.brizzs.a1musicplayer.model.PlayList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = [PlayList::class], version = 1, exportSchema = false)
abstract class PlayListDB : RoomDatabase() {
    abstract fun playListDao(): PlayListDao

    companion object {
        private const val NO_OF_THREADS = 10
        @JvmField
        var playlistWriteExecutor: ExecutorService = Executors.newFixedThreadPool(NO_OF_THREADS)

        @Volatile
        private var instance: PlayListDB? = null

        @JvmStatic
        fun getInstance(context: Context): PlayListDB {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    PlayListDB::class.java, "playlist_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}
