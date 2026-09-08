package com.brizzs.a1musicplayer.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.brizzs.a1musicplayer.dao.AlbumDao
import com.brizzs.a1musicplayer.model.Album
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = [Album::class], version = 1, exportSchema = false)
abstract class AlbumDB : RoomDatabase() {
    abstract fun albumDao(): AlbumDao

    companion object {
        private const val NO_OF_THREADS = 10
        @JvmField
        var albumWriteExecutor: ExecutorService = Executors.newFixedThreadPool(NO_OF_THREADS)

        @Volatile
        private var instance: AlbumDB? = null

        @JvmStatic
        fun getInstance(context: Context): AlbumDB {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AlbumDB::class.java, "album_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}
