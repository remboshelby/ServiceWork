package com.inc.evil.servicework.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.inc.evil.servicework.Receiver;

public class BatteryService extends Service {

    private Receiver batteryLevelNotification;
    private boolean isRegistred;

    @androidx.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        batteryLevelNotification = new Receiver();

        BatteryLowReceiver batteryLowReceiver = new BatteryLowReceiver();
        BatteryOKReceiver batteryOKReceiver = new BatteryOKReceiver();

        IntentFilter ifBatteryLow = new IntentFilter();
        ifBatteryLow.addAction(Intent.ACTION_BATTERY_LOW);

        IntentFilter ifBatteryOK = new IntentFilter();
        ifBatteryOK.addAction(Intent.ACTION_BATTERY_OKAY);

        registerReceiver(batteryLowReceiver, ifBatteryLow);
        registerReceiver(batteryOKReceiver, ifBatteryOK);

        return super.onStartCommand(intent, flags, startId);
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
}
