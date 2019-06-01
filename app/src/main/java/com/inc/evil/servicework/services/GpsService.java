package com.inc.evil.servicework.services;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.inc.evil.servicework.R;

import java.util.Date;

public class GpsService extends Service implements LocationListener {

    private Context mContext;
    boolean checkGPS = false;
    boolean canGetLocation = false;

    Location location;
    double latitude;
    double longitude;

    private static final int MIN_DISTANCE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    private static final String TAG = GpsService.class.getSimpleName();
    private boolean isGpsTrackingON = false;

    protected LocationManager locationManager;

    private static final String ACTION_STOP_GPS_TRACKING = "stop_gps";
    private static final String ACTION_STOP_GPS_CANCEL = "stop_gps_hop";
    private ActionNotificationReceiver receiverGspTurnOff;
    private CancelNotificationReceiver cancelNotificationReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        if (!isGpsTrackingON) {
            createReceivers();
            startTracking();
            Log.d(TAG, "startTracking");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Log.d(TAG, "onCreate");
    }

    private void createReceivers() {
        IntentFilter gpsStop = new IntentFilter();
        IntentFilter gpsCancel = new IntentFilter();

        receiverGspTurnOff = new ActionNotificationReceiver();
        cancelNotificationReceiver = new CancelNotificationReceiver();

        gpsStop.addAction(ACTION_STOP_GPS_TRACKING);
        gpsCancel.addAction(ACTION_STOP_GPS_CANCEL);

        registerReceiver(receiverGspTurnOff, gpsStop);
        registerReceiver(cancelNotificationReceiver, gpsCancel);
    }

    private void startTracking() {
        Intent intent = new Intent();
        intent.setAction(ACTION_STOP_GPS_TRACKING);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 1000, 6000, pendingIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiverGspTurnOff);
        unregisterReceiver(cancelNotificationReceiver);
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {
        locationManager = (LocationManager) mContext
                .getSystemService(LOCATION_SERVICE);
        checkGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!checkGPS) {
            Toast.makeText(this, getString(R.string.gps_is_off), Toast.LENGTH_SHORT).show();
        } else {
            canGetLocation = true;
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_FOR_UPDATES, this);
            if (locationManager != null) {
                location = locationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        }
        return location;
    }

    public class ActionNotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            newNotificateWithButton(getLocation(), mContext);
            Log.d(TAG, "newNotificate");
        }

    }

    public class CancelNotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(receiverGspTurnOff);
            setGpsTrackingON(false);
            Log.d(TAG, "unregisterReceiver");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        String t = "dsdfsdf";
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


    private void newNotificateWithButton(Location location, Context context) {
        Intent stopGpsTrackingIntent = new Intent();
        stopGpsTrackingIntent.setAction(ACTION_STOP_GPS_CANCEL);
        PendingIntent stopGpsPendingIntent = PendingIntent.getBroadcast(context, 0, stopGpsTrackingIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notify_001")
                .setContentTitle(context.getString(R.string.battery_discharging))
                .setContentText(context.getString(R.string.gps_info, String.valueOf(location.getLatitude()).substring(0, 5),
                        String.valueOf(location.getLongitude()).substring(0, 5)))
                .setSmallIcon(R.drawable.ic_location_on_black_24dp)
                .setDefaults(Notification.DEFAULT_SOUND)
                .addAction(R.drawable.ic_close_black_24dp, "Сlose", stopGpsPendingIntent)
                .setAutoCancel(true);

        long time = new Date().getTime();
        String tmpStr = String.valueOf(time);
        String last4Str = tmpStr.substring(tmpStr.length() - 5);
        int notificationId = 250;
//        int notificationId = Integer.valueOf(last4Str);

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

    public void setGpsTrackingON(boolean gpsTrackingON) {
        isGpsTrackingON = gpsTrackingON;
    }
}
