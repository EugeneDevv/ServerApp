package com.example.thunderbolt_108.serverapp.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;

import com.example.thunderbolt_108.serverapp.R;

public class NotificationHelper extends ContextWrapper {

    private static final String THUNDERBOLT_108_ID="com.example.thunderbolt_108.serverapp.THUNDERBOLT_108";
    private static final String THUNDERBOLT_108_NAME="Food App";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel Thunderbolt_108=new NotificationChannel(THUNDERBOLT_108_ID,
                THUNDERBOLT_108_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        Thunderbolt_108.enableLights(true);
        Thunderbolt_108.enableVibration(true);
        Thunderbolt_108.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(Thunderbolt_108);
    }

    public NotificationManager getManager() {
        if (manager==null)
            manager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getNotificationChannel(String title, String body, PendingIntent contentIntent, Uri soundUri)
    {
        return new Notification.Builder(getApplicationContext(),THUNDERBOLT_108_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.picture)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.mipmap.picture))
                .setSound(soundUri)
                .setAutoCancel(true);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getNotificationChannel(String title, String body, Uri soundUri)
    {
        return new Notification.Builder(getApplicationContext(),THUNDERBOLT_108_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.picture)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.mipmap.picture))
                .setSound(soundUri)
                .setAutoCancel(true);
    }
}
