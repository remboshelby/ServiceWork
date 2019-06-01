package com.inc.evil.servicework.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.inc.evil.servicework.R;

import java.util.Date;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class GpsService2 extends IntentService implements LocationListener {
    private static final int FOREGROUND_ID = 419;
    private Context mContext;

    private Location location;
    private double latitude;
    private double longitude;

    String channelId = "YOUR_CHANNEL_ID";
    private NotificationChannel channel;


    private static final String ACTION_STOP_GPS_TRACKING = "stop_gps";
    private ActionNotificationReceiver receiverGspTurnOff;

    public GpsService2() {
        super(GpsService2.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        receiverGspTurnOff = new ActionNotificationReceiver();
        IntentFilter gpsStop = new IntentFilter();
        gpsStop.addAction(ACTION_STOP_GPS_TRACKING);

        registerReceiver(receiverGspTurnOff, gpsStop);
        mContext = getApplicationContext();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiverGspTurnOff);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        String t = "fdsfdf";
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        startForeground(FOREGROUND_ID, buildForegroundNotification());

        sendBroadcast(new Intent(ACTION_STOP_GPS_TRACKING));
//        newNotificateWithButton(mContext);
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    private Notification buildForegroundNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "notify_001")
                .setContentTitle(mContext.getString(R.string.battery_discharging))
                .setSmallIcon(R.drawable.ic_location_on_black_24dp)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }
        return (builder.build());
    }
    private void newNotificateWithButton(Context context) {
        long time = new Date().getTime();
        String tmpStr = String.valueOf(time);
        String last4Str = tmpStr.substring(tmpStr.length() - 5);
        int notificationId = Integer.valueOf(last4Str);

        Intent stopGpsTrackingIntent = new Intent(ACTION_STOP_GPS_TRACKING);
        stopGpsTrackingIntent.setClass(this, ActionNotificationReceiver.class);
        stopGpsTrackingIntent.setAction(ACTION_STOP_GPS_TRACKING);
        stopGpsTrackingIntent.putExtra(EXTRA_NOTIFICATION_ID,0);
        PendingIntent stopGpsPendingIntent = PendingIntent.getBroadcast(this, 0, stopGpsTrackingIntent, 0);

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_close_black_24dp, "Сlose", stopGpsPendingIntent).build();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notify_001")
                .setContentTitle(context.getString(R.string.battery_discharging))
                .setContentText("fdsffsdfsdf")
//                .setContentText(context.getString(R.string.gps_info, String.valueOf(location.getLatitude()).substring(0, 5),
//                        String.valueOf(location.getLongitude()).substring(0, 5)))
                .setSmallIcon(R.drawable.ic_location_on_black_24dp)
                .setDefaults(Notification.DEFAULT_SOUND)

                .addAction(action)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)context.
                getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(channelId);
        }
        notificationManager.notify(0, builder.build());

    }
    public class ActionNotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String t ="fdsdfs";
        }
    }
}
