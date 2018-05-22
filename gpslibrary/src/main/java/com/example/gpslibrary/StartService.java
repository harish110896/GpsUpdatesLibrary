package com.example.gpslibrary;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class StartService extends Service {
    private Context mContext;
    GPSTracker gps;
    public StartService() {

    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(),"Started",Toast.LENGTH_LONG).show();
        gps=new GPSTracker(this);
        gpsupdates();
    }

    private void gpsupdates()
    {
        final Handler ha=new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function
                gps.getLocation();
                if(gps.canGetLocation()){
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                            + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                }else{
                    gps.showSettingsAlert();
                }
                SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("Service change", Context.MODE_PRIVATE);
                int service=sharedpreferences.getInt("service",1);
                if(service == 1)
                {
                    Toast.makeText(getApplicationContext(),"Boot service running",Toast.LENGTH_LONG).show();
                    ha.postDelayed(this, 5000);
                }
                else if(service == 0)
                {
                    Toast.makeText(getApplicationContext(),"Boot service stopped",Toast.LENGTH_LONG).show();
                    gps.stopUsingGPS();
                    stopSelf();
                    ha.removeCallbacksAndMessages(this);
                }
            }
        }, 10000);
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);

        return result == PackageManager.PERMISSION_GRANTED;
    }

}
