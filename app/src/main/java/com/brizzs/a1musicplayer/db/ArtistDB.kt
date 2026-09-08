package com.brizzs.a1musicplayer.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.brizzs.a1musicplayer.dao.ArtistDao
import com.brizzs.a1musicplayer.model.Artist
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = [Artist::class], version = 1, exportSchema = false)
abstract class ArtistDB : RoomDatabase() {
    abstract fun artistDao(): ArtistDao

    companion object {
        private const val NO_OF_THREADS = 10
        @JvmField
        var artistWriteExecutor: ExecutorService = Executors.newFixedThreadPool(NO_OF_THREADS)

        @Volatile
        @JvmField
        var instance: ArtistDB? = null

        @JvmStatic
        fun getInstance(context: Context): ArtistDB {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ArtistDB::class.java, "artist_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}
