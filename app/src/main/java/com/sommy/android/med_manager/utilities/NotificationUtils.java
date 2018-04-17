package com.sommy.android.med_manager.utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.sommy.android.med_manager.R;
import com.sommy.android.med_manager.ui.MainActivity;

/**
 * Created by somto on 4/15/18.
 */

public class NotificationUtils {
    /*
    * This notification ID can be used to access our notification after we've displayed it. This
    * can be handy when we need to cancel the notification, or perhaps update it. This number is
    * arbitrary and can be set to whatever you like. 1111 is in no way significant.
    */
    private static final int MEDICATION_REMINDER_NOTIFICATION_ID = 1111;
    /**
     * This pending intent id is used to uniquely reference the pending intent
     */
    private static final int MEDICATION_REMINDER_PENDING_INTENT_ID = 2222;

    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void remindUserToTakeMedication(Context context) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "medChannelId")
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.background_img)
//                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.medication_reminder_notification_title))
                .setContentText(context.getString(R.string.medication_reminder_notification_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.medication_reminder_notification_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }


        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        /* MEDICATION_REMINDER_NOTIFICATION_ID allows you to update or cancel the notification later on */
        notificationManager.notify(MEDICATION_REMINDER_NOTIFICATION_ID, notificationBuilder.build());
    }

    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(
                context,
                MEDICATION_REMINDER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context) {
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.background_img);
        return largeIcon;
    }
}
