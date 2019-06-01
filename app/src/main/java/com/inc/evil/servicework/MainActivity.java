package com.inc.evil.servicework;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.inc.evil.servicework.services.BatteryService;
import com.inc.evil.servicework.services.GpsService;
import com.inc.evil.servicework.services.GpsService2;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final int ALL_PERMISSIONS_RESULT = 1;

    @BindView(R.id.btn_bat_ser)
    Button btn_bat_ser;
    @BindView(R.id.btn_gps_service)
    Button btn_gps_service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btn_bat_ser)
    public void btn_bat_ser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, BatteryService.class));
        } else {
            startService(new Intent(this, BatteryService.class));
        }
    }

    @OnClick(R.id.btn_gps_service)
    public void btnGpsService() {

        ArrayList<String>  permission = new ArrayList<>();
        ArrayList<String>  requestedPermission = new ArrayList<>();

        permission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permission.add(Manifest.permission.ACCESS_FINE_LOCATION);

        requestedPermission = permissionToRequest(permission);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(requestedPermission.size()>0){
                requestPermissions(requestedPermission.
                        toArray(new String[requestedPermission.size()]),  ALL_PERMISSIONS_RESULT);
            }
            else {
                startGpsService();
            }
        }
    }


    private ArrayList<String> permissionToRequest(ArrayList<String> wantedPermissons){
        ArrayList<String> result = new ArrayList<>();
        for (String perm : wantedPermissons) {
            if (!hasPermission(perm)){
                result.add(perm);
            }

        }

        return result;
    }

    private boolean hasPermission(String perm) {
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            return checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void startGpsService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startService(new Intent(this, GpsService.class));
        } else {
            startService(new Intent(this, GpsService2.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case ALL_PERMISSIONS_RESULT:{
                if (grantResults.length>0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        startGpsService();
                }
                else {
                    Toast.makeText(this, getString(R.string.permission_not_granted), Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
