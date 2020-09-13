package com.example.birthdayreminder;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import static com.example.birthdayreminder.NotificationUtilsKt.createChannel;
import static com.example.birthdayreminder.NotificationUtilsKt.sendNotification;

public class BootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = ContextCompat.getSystemService(context,
                NotificationManager.class);

        createChannel(notificationManager, "MGProgrammsBirthdayReminderChannel",
                "birthdays");

        sendNotification(
                notificationManager,
                "title",
                "message",
                null,
                "birthdays",
                context
        );

        // AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // PendingIntent pi = PendingIntent.getService(context, 0, new Intent(context, MyService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        // am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, interval, pi);
    }
}
