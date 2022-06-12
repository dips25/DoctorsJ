package com.example.doctorsj;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class DoctorsJ extends Application {

    public static final String CHANNEL_ID = "notif_channel_id";
    public static final String CHANNEL_NAME = "notif_channel_name";
    public static final String CHANNEL_DESCRIPTION = "notif_channel_desciption";

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID , CHANNEL_NAME , NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(CHANNEL_DESCRIPTION);

            notificationManager.createNotificationChannel(notificationChannel);

        }

    }
}
