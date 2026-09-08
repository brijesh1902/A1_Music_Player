package com.brizzs.a1musicplayer.service

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.content.IntentSender
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

class AppUpdateService : Service() {
    private var mAppUpdateManager: AppUpdateManager? = null

    companion object {
        private const val RC_APP_UPDATE = 11
        private const val TAG = "AppUpdateService"
    }

    override fun onCreate() {
        super.onCreate()
        mAppUpdateManager = AppUpdateManagerFactory.create(this).apply {
            registerListener(installStateUpdatedListener)
            appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                ) {
                    try {
                        startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.FLEXIBLE,
                            applicationContext as Activity,
                            RC_APP_UPDATE
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }
                } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdate()
                } else {
                    Log.e(TAG, "checkForAppUpdateAvailability: something else")
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private val installStateUpdatedListener: InstallStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackbarForCompleteUpdate()
        } else if (state.installStatus() == InstallStatus.INSTALLED) {
            mAppUpdateManager?.unregisterListener(installStateUpdatedListener)
        } else {
            Log.i(TAG, "InstallStateUpdatedListener: state: " + state.installStatus())
        }
    }

    private fun popupSnackbarForCompleteUpdate() {
        val message = "New update is ready to install!"
        val it = Intent("EVENT_SNACKBAR").apply {
            putExtra("key", message)
        }
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(it)
    }

    override fun onDestroy() {
        super.onDestroy()
        mAppUpdateManager?.unregisterListener(installStateUpdatedListener)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
