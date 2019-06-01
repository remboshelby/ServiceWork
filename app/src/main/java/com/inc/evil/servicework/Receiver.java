package com.inc.evil.servicework;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import java.util.Date;

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        newNotificateBattery(level, context);
    }
    private void newNotificateBattery(int battery_level, Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notify_001")
                .setContentTitle(context.getString(R.string.battery_discharging))
                .setContentText(context.getString(R.string.battery_level,  battery_level))
                .setSmallIcon(R.drawable.ic_battery_charging_20_black_24dp)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true);


        long time = new Date().getTime();
        String tmpStr = String.valueOf(time);
        String last4Str = tmpStr.substring(tmpStr.length() - 5);
        int notificationId = Integer.valueOf(last4Str);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "YOUR_CHANNEL_ID";
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }
        notificationManager.notify(notificationId, builder.build());
    }

}
