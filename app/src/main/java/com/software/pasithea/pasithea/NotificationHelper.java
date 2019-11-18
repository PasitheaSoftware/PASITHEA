/* Copyright (C) 2019 François Laforgia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * @Author: François Laforgia (f.laforgia@logicielpasithea.fr)
 * @Date: April 10th 2019
 *
 */
package com.software.pasithea.pasithea;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

class NotificationHelper extends ContextWrapper {
    private static final String TAG = "NotificationHelper";
    public static final String CHANNEL_ID = "pro.byzance.pasithea.sttChannel";
    public static final String CHANNEL_NAME = "PASITHEA";

    private NotificationManager mNotificationManager;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannels();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannels(){
        NotificationChannel SttChannel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(SttChannel);

    }

    public NotificationManager getManager(){
        if (mNotificationManager == null){
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }

    public NotificationCompat.Builder getChannelNotification(String title, String message){
        return new NotificationCompat.Builder(Environment.getGlobalContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.fond_ecran)
                .setOngoing(true);
    }

    public void stopNotification(){
        mNotificationManager.cancelAll();
    }
}
