package com.brizzs.a1musicplayer.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.android.exoplayer2.ExoPlayer

class PlayService : Service() {
    lateinit var exoPlayer: ExoPlayer

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(this).build()
    }
}
