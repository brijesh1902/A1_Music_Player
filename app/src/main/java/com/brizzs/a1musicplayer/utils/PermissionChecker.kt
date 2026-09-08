package com.brizzs.a1musicplayer.utils

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.brizzs.a1musicplayer.ui.main.MainActivity

class PermissionChecker(private val context: MainActivity) {
    private val permissions_12 = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE)
    private val permissions_13 = arrayOf(Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_PHONE_STATE)

    private fun performAction() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.POST_NOTIFICATIONS)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(
                        Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.POST_NOTIFICATIONS,
                        Manifest.permission.USE_FULL_SCREEN_INTENT
                    ),
                    100
                )
            }
        }
    }

    private fun showPermissionExplanationDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Permission Required")
        builder.setMessage("This permission is required to access media files.")
        builder.setPositiveButton("Grant Permission") { _, _ -> requestPermission() }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_MEDIA_AUDIO)) {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(
                        Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.POST_NOTIFICATIONS,
                        Manifest.permission.USE_FULL_SCREEN_INTENT
                    ),
                    100
                )
            } else {
                openAppPermissionSettings()
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE),
                    100
                )
            } else {
                openAppPermissionSettings()
            }
        }
    }

    private fun openAppPermissionSettings() {
        var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri

        if (intent.resolveActivity(context.packageManager) == null) {
            intent = Intent(Settings.ACTION_APPLICATION_SETTINGS)
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                performAction()
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    openAppPermissionSettings()
                }
            }
        }
    }

    fun areAllPermissionsGranted(): Boolean {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) permissions_13 else permissions_12
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}
