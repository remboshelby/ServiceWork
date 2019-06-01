package com.inc.evil.servicework.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.inc.evil.servicework.R;

import java.util.Date;

public class BatteryService extends Service {

    private BatteryReceiver batteryLevelNotification;
    private boolean isRegistred;


    private static final String TAG = BatteryService.class.getSimpleName() ;

    @androidx.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        if (!isRegistred){
            createReceivers();
            Log.d(TAG, "createReceivers");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void createReceivers() {
        batteryLevelNotification = new BatteryReceiver();
        BatteryLowReceiver batteryLowReceiver = new BatteryLowReceiver();
        BatteryOKReceiver batteryOKReceiver = new BatteryOKReceiver();

        IntentFilter ifBatteryLow = new IntentFilter();
        ifBatteryLow.addAction(Intent.ACTION_BATTERY_LOW);

        IntentFilter ifBatteryOK = new IntentFilter();
        ifBatteryOK.addAction(Intent.ACTION_BATTERY_OKAY);

        registerReceiver(batteryLowReceiver, ifBatteryLow);
        registerReceiver(batteryOKReceiver, ifBatteryOK);
    }

    public class BatteryLowReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            IntentFilter ifBatteryLevel = new IntentFilter();
            ifBatteryLevel.addAction(Intent.ACTION_BATTERY_CHANGED);
            registerReceiver(batteryLevelNotification, ifBatteryLevel);
            isRegistred = true;
        }
    }

    public class BatteryOKReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isRegistred) unregisterReceiver(batteryLevelNotification);
        }
    }
    class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            newNotificateBattery(level, context);
        }

        private void newNotificateBattery(int battery_level, Context context) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notify_001")
                    .setContentTitle(context.getString(R.string.battery_discharging))
                    .setContentText(context.getString(R.string.battery_level, battery_level))
                    .setSmallIcon(R.drawable.ic_battery_charging_20_black_24dp)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true);


            long time = new Date().getTime();
            String tmpStr = String.valueOf(time);
            String last4Str = tmpStr.substring(tmpStr.length() - 5);
            int notificationId = Integer.valueOf(last4Str);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

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
}
