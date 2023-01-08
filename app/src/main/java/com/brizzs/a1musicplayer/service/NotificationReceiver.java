package com.brizzs.a1musicplayer.service;

import static android.content.Context.TELEPHONY_SERVICE;
import static com.brizzs.a1musicplayer.utils.MyApplication.NEXT;
import static com.brizzs.a1musicplayer.utils.MyApplication.PAUSE;
import static com.brizzs.a1musicplayer.utils.MyApplication.PLAY;
import static com.brizzs.a1musicplayer.utils.MyApplication.PREVIOUS;
import static com.brizzs.a1musicplayer.utils.MyApplication.REMOVE;
import static com.brizzs.a1musicplayer.utils.Common.actionName;
import static com.brizzs.a1musicplayer.utils.Common.isServiceRunning;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

public class NotificationReceiver extends BroadcastReceiver {

    private boolean callStateListenerRegistered = false;
    Context context;
    boolean isCall = false, isService;
    TelephonyManager telephonyManager;
    Intent serviceIntent;

    public void showToast(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceive(Context c, Intent intent) {

        context = c;
        telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);

        String action_name = intent.getAction();
        serviceIntent = new Intent(context, MusicService.class);

        isService = isServiceRunning(MusicService.class.getName(), context);

        registerCallStateListener();

        if (actionName != null) {
            switch (action_name) {
                case PLAY:
                    serviceIntent.putExtra(actionName, PLAY);
                    context.startService(serviceIntent);
                    break;
                case NEXT:
                    serviceIntent.putExtra(actionName, NEXT);
                    context.startService(serviceIntent);
                    break;
                case PREVIOUS:
                    serviceIntent.putExtra(actionName, PREVIOUS);
                    context.startService(serviceIntent);
                    break;
                /*case REPLAY_10:
                    serviceIntent.putExtra(actionName, REPLAY_10);
                    context.startService(serviceIntent);
                    break;
                case FORWARD_10:
                    serviceIntent.putExtra(actionName, FORWARD_10);
                    context.startService(serviceIntent);
                    break;*/
                case REMOVE:
                    serviceIntent.putExtra(actionName, REMOVE);
                    context.startService(serviceIntent);
                    break;
            }
        }

        if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);

            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    serviceIntent.putExtra(actionName, PLAY);
                    context.startService(serviceIntent);
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    serviceIntent.putExtra(actionName, NEXT);
                    context.startService(serviceIntent);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    serviceIntent.putExtra(actionName, PREVIOUS);
                    context.startService(serviceIntent);
                    break;
            }
        }
    }

    private void registerCallStateListener() {
        if (!callStateListenerRegistered) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    telephonyManager.registerTelephonyCallback(context.getMainExecutor(), callStateListener);
                    callStateListenerRegistered = true;
                }
            } else {
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
                callStateListenerRegistered = true;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private abstract static class CallStateListener extends TelephonyCallback implements TelephonyCallback.CallStateListener {
        @Override
        abstract public void onCallStateChanged(int state);
    }

    public CallStateListener callStateListener = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ? new CallStateListener() {
                @Override
                public void onCallStateChanged(int state) {
                    if (isService) {
                        if (state == (TelephonyManager.CALL_STATE_IDLE)) {
                            if (isCall) {
                                isCall = false;
                                serviceIntent.putExtra(actionName, PLAY);
                                context.startService(serviceIntent);
                            }
                        }
                        if (state == (TelephonyManager.CALL_STATE_RINGING)) {
                            isCall = true;
                            serviceIntent.putExtra(actionName, PAUSE);
                            context.startService(serviceIntent);
                        }
                    }
                }
            } : null;

    public PhoneStateListener phoneStateListener = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ? null : new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            if (isService) {
                if (state == (TelephonyManager.CALL_STATE_IDLE)) {
                    if (isCall) {
                        isCall = false;
                        serviceIntent.putExtra(actionName, PLAY);
                        context.startService(serviceIntent);
                    }
                }
                if (state == (TelephonyManager.CALL_STATE_RINGING)) {
                    isCall = true;
                    serviceIntent.putExtra(actionName, PAUSE);
                    context.startService(serviceIntent);
                }
            }
        }
    };

    public String ComponentName() {
        return this.getClass().getName();
    }
}
