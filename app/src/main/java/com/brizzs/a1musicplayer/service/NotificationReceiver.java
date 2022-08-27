package com.brizzs.a1musicplayer.service;

import static com.brizzs.a1musicplayer.utils.App.NEXT;
import static com.brizzs.a1musicplayer.utils.App.PAUSE;
import static com.brizzs.a1musicplayer.utils.App.PLAY;
import static com.brizzs.a1musicplayer.utils.App.PREVIOUS;
import static com.brizzs.a1musicplayer.utils.App.REMOVE;
import static com.brizzs.a1musicplayer.utils.Common.actionName;
import static com.brizzs.a1musicplayer.utils.Common.isServiceRunning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {

    String TAG = "RECEIVER", phoneNumber;
    Context c;
    boolean isCall = false;

    public void showToast(String s) {
        Toast.makeText(c, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action_name = intent.getAction();
        Intent serviceIntent = new Intent(context, MusicService.class);

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        boolean isService = isServiceRunning(MusicService.class.getName(), context);

        telephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
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
        }, PhoneStateListener.LISTEN_CALL_STATE);


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

       /* if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
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
        }*/
    }

    public String ComponentName() {
        return this.getClass().getName();
    }
}
