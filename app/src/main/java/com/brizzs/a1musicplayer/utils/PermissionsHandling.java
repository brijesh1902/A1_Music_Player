package com.brizzs.a1musicplayer.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsHandling {

    Context context;

    public PermissionsHandling(Context context) {
        this.context=context;
    }

    public boolean isPermissionGranted() {

        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isRequestPermissionable() {
        return ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_PHONE_STATE);
    }

    public void showAlertDialog(final int REQUEST_CODE) {
        androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle("Grant permissions");
        builder.setMessage("Read Storage");
        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                ActivityCompat.requestPermissions(
                        (Activity) context,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                        REQUEST_CODE
                );
            }
        });

        androidx.appcompat.app.AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    public void requestPermission(int REQUEST_CODE) {
        ActivityCompat.requestPermissions(
                (Activity) context,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                REQUEST_CODE
        );

    }
}
