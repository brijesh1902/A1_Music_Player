package com.brizzs.a1musicplayer.utils

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.StrictMode
import com.brizz.linguistic.LanguageSDK
import com.brizz.linguistic.LingualSDK
import com.brizzs.a1musicplayer.BuildConfig
import com.brizzs.a1musicplayer.R
import com.brizzs.a1musicplayer.ads.AppOpenManager
import com.brizzs.a1musicplayer.dao.AlbumDao
import com.brizzs.a1musicplayer.dao.ArtistDao
import com.brizzs.a1musicplayer.dao.PlayListDao
import com.brizzs.a1musicplayer.db.AlbumDB
import com.brizzs.a1musicplayer.db.ArtistDB
import com.brizzs.a1musicplayer.db.PlayListDB

class MyApplication : Application() {

    private var playListDao: PlayListDao? = null
    private var albumDao: AlbumDao? = null
    private var artistDao: ArtistDao? = null
    var appOpenManager: AppOpenManager? = null

    fun getAlbumDao(): AlbumDao {
        if (albumDao == null) {
            val db = AlbumDB.getInstance(applicationContext)
            albumDao = db.albumDao()
        }
        return albumDao!!
    }

    fun getArtistDao(): ArtistDao {
        if (artistDao == null) {
            val db = ArtistDB.getInstance(applicationContext)
            artistDao = db.artistDao()
        }
        return artistDao!!
    }

    fun getPlayListDao(): PlayListDao {
        if (playListDao == null) {
            val db = PlayListDB.getInstance(applicationContext)
            playListDao = db.playListDao()
        }
        return playListDao!!
    }

    override fun onCreate() {
        super.onCreate()
        instanceVal = this

        LingualSDK.init(this, R.string::class.java.fields)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL, NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                description = DESCRIPTION
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    setAllowBubbles(true)
                }
                importance = NotificationManager.IMPORTANCE_HIGH
                enableVibration(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }

        if (BuildConfig.DEBUG) {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
        }
    }

    companion object {
        const val CHANNEL = "A1 Musics"
        const val DESCRIPTION = "Music Player"
        const val PREVIOUS = "PREVIOUS"
        const val NEXT = "NEXT"
        const val PLAY = "PLAY"
        const val PAUSE = "PAUSE"
        const val REPLAY_10 = "REPLAY_10"
        const val FORWARD_10 = "FORWARD_10"
        const val REMOVE = "REMOVE"
        const val NAME = "PLAYER"

        private lateinit var instanceVal: MyApplication

        private var sharePreference: SharePreference? = null

        @JvmStatic
        fun getInstance(): MyApplication = instanceVal

        @JvmStatic
        fun getSharePreference(): SharePreference {
            if (sharePreference == null) {
                sharePreference = SharePreference(getInstance())
            }
            return sharePreference!!
        }
    }
}
