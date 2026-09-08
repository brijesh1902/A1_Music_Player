package com.brizzs.a1musicplayer.utils

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.brizzs.a1musicplayer.R
import com.brizzs.a1musicplayer.model.Songs
import com.brizzs.a1musicplayer.ui.playing.PlayActivity.position
import com.brizzs.a1musicplayer.ui.playing.PlayActivity.songsList
import com.brizzs.a1musicplayer.utils.MyApplication
import com.bumptech.glide.Glide

object Common {
    @JvmField
    var alb_Uri: Uri = Uri.parse("content://media/external/audio/albumart")
    @JvmField
    var art_Uri: Uri = Uri.parse("content://media/external/audio/artistart")

    @JvmField
    var ISGRIDVIEW = true
    const val SPAN_COUNT_ONE = 1
    @JvmField
    var SPAN_COUNT = 2

    @JvmField
    var current_play: Songs? = null
    @JvmField
    var current_playlist: List<Songs>? = null

    @JvmField
    var duration = "duration"
    @JvmField
    var current_list = "current_list"
    @JvmField
    var current_album = "current_album"
    @JvmField
    var current_position = 0
    @JvmField
    var servicePosition = "servicePosition"
    @JvmField
    var actionName = "actionName"

    @JvmField
    var recently = "recently"
    @JvmField
    var album = "album"
    @JvmField
    var artists = "artists"
    @JvmField
    var playlist = "playlist"

    const val APP_OPENED = "APP_OPENED"

    const val MUSIC_PLAYED = "MUSIC_FILE"
    const val MUSIC_FILE = "MUSIC_FILE"
    const val MUSIC_ARTIST = "MUSIC_ARTIST"
    const val MUSIC_NAME = "MUSIC_NAME"
    const val MUSIC_IMG = "MUSIC_IMG"

    const val FAVOURITES = "Favourites"
    @JvmField
    var isUpdated = false

    @JvmField
    var value = ""
    @JvmField
    var name = ""
    @JvmField
    var image = ""
    @JvmField
    var artist = ""
    @JvmField
    var TITLE = ""
    @JvmField
    var IMAGE = ""
    @JvmField
    var ARTIST = ""
    @JvmField
    var SHOW_MINI_PLAYER = false

    @JvmStatic
    fun createTime(duration: Int): String {
        val min = duration / 1000 / 60
        val sec = duration / 1000 % 60
        val minute = if (min < 10) "0$min" else "$min"
        val secs = if (sec < 10) "0$sec" else "$sec"
        return "$minute:$secs"
    }

    @JvmStatic
    fun getImage(uri: String?): ByteArray? {
        val metadataRetriever = MediaMetadataRetriever()
        uri?.let { metadataRetriever.setDataSource(it) }
        return metadataRetriever.embeddedPicture
    }

    @JvmStatic
    fun isServiceRunning(serviceName: String, context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceName == service.service.className) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun setView(context: Context, title: TextView, artist: TextView, image: ImageView) {
        if (!songsList.isNullOrEmpty() && position >= 0 && position < songsList.size) {
            Glide.with(context).load(songsList[position].image)
                .placeholder(R.drawable.music_note_24)
                .error(R.drawable.music_note_24)
                .into(image)

            title.text = songsList[position].name
            artist.text = songsList[position].artist
        }
    }

    @JvmStatic
    fun appInstalledOrNot(url: String, context: Context): Boolean {
        val packageManager = context.packageManager
        return try {
            packageManager.getPackageInfo(url, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    @JvmStatic
    fun sendNotification(applicationContext: Context) {
        val appPackageName = applicationContext.packageName
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 5,
            Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val title = "UPDATE AVAILABLE"
        val msg = "A new version of A1 Music Player is available on Playstore. Click to update now."
        val thumbnail = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.googleplay)
        val manager = applicationContext.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(applicationContext, MyApplication.CHANNEL)
                .setSmallIcon(R.drawable.ic_notify)
                .setColor(ContextCompat.getColor(applicationContext, R.color.primary))
                .setLargeIcon(thumbnail)
                .setContentTitle(title)
                .setContentText(msg)
                .setContentIntent(pendingIntent)
                .setStyle(Notification.BigTextStyle())
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .build()
        } else {
            NotificationCompat.Builder(applicationContext, MyApplication.CHANNEL)
                .setSmallIcon(R.drawable.ic_notify)
                .setColor(ContextCompat.getColor(applicationContext, R.color.primary))
                .setLargeIcon(thumbnail)
                .setContentTitle(title)
                .setContentText(msg)
                .setContentIntent(pendingIntent)
                .setStyle(NotificationCompat.BigTextStyle())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .build()
        }
        manager.notify(1, notification)
    }
}
