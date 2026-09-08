package com.brizzs.a1musicplayer.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.brizzs.a1musicplayer.dao.SongsDao
import com.brizzs.a1musicplayer.model.Songs
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = [Songs::class], version = 1, exportSchema = false)
abstract class SongsDB : RoomDatabase() {
    abstract fun songsDao(): SongsDao

    companion object {
        private const val NO_OF_THREADS = 10
        @JvmField
        var databaseWriteExecutor: ExecutorService = Executors.newFixedThreadPool(NO_OF_THREADS)

        @Volatile
        private var instance: SongsDB? = null

        @JvmStatic
        fun getDatabase(context: Context): SongsDB {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    SongsDB::class.java, "songs_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}
