package com.woodplantation.werwolf;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.woodplantation.werwolf.activities.LobbyActivity;

/**
 * Created by Sebu on 12.11.2016.
 */

public class Notification {

    private static final int NOTIFICATION_ID = 1;

    public static final String INTENT_COMING_FROM_NOTIFICATION = "coming_from_notification";

    public static void createNotification(Context context) {

        Intent notificationIntent = new Intent(context, LobbyActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra(INTENT_COMING_FROM_NOTIFICATION, true);
        PendingIntent pi = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_people_outline);
        builder.setContentTitle(context.getString(R.string.notification_title));
        builder.setContentText(context.getString(R.string.notification_text));
        builder.setContentIntent(pi);
        builder.setAutoCancel(false);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public static void deleteNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

}
