package ism.android;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.util.List;

/**
 * Created by Raj on 14/04/16.
 */
public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("alert");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
        Log.d(TAG, "isBackgroundRunning: " + isAppRunning(getApplicationContext()));


//        if (from.startsWith("/topics/")) {
//            // message received from some topic.
//        } else {
//            // normal downstream message.
//        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(message, isAppRunning(getApplicationContext()));
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message, boolean isAppRunning) {

        SharedPreferences preferences = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.PREFS_NAME), getApplicationContext().MODE_PRIVATE);
        String regGUID = preferences.getString(getApplicationContext().getString(R.string.REGISTERED_GUID), null);
        //String strNewsBanner = preferences.getString(getString(R.string.NEWS_BANNER), null);
        int orgID = preferences.getInt(getApplicationContext().getString(R.string.ORG_ID), 0);
        String serviceLocation = preferences.getString(getApplicationContext().getString(R.string.WEB_SERVICE_LOCATION), null);


        if (isAppRunning) {
            String msg = "You have updates waiting on the Server. Please login to view them";
            if (null != regGUID && orgID != 0 && serviceLocation != null) {
                msg = "You have updates waiting on the Server. Please refresh to view them";
            }
            Intent intent = new Intent("pushNotification");
            sendLocationBroadcast(intent, msg);
        } else {

            ActivityStringInfo.shouldRefreshed = true;
            Intent intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.white)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }

    public static boolean isAppRunning(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            System.out.println(processInfo.pkgList);
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND || processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_TOP_SLEEPING) {
                for (String activeProcess : processInfo.pkgList) {
                    System.out.println(activeProcess.toString());
                    if (activeProcess.equals(context.getPackageName())) {
                        return true;
                    }
                }
            }
        }


        return false;
    }

    private void sendLocationBroadcast(Intent intent, String message) {
        intent.putExtra("msg", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

}
