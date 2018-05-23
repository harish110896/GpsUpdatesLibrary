package com.example.gpslibrary;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.service.voice.VoiceInteractionSession;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.Toast;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class StartService extends Service {
    private static final String TAG = "Service";
    private Context mContext;
    GPSTracker gps;
    public static long changedtime=0;
    long timertime=0;
    Chronometer chronometer;
    long updatedtime=0;
    int timer=0,service=0;

    public StartService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        gps=new GPSTracker(this);
        //Toast.makeText(getApplicationContext(),"Started",Toast.LENGTH_LONG).show();
        chronometer=new Chronometer(this);
        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("Service change", Context.MODE_PRIVATE);
        timer=sharedpreferences.getInt("timer",0);
        timertime=sharedpreferences.getLong("timertime",0);
        //timertime=timertime-changedtime;
        if(timer == 0)
        {
            Toast.makeText(getApplicationContext(),"Started normal",Toast.LENGTH_LONG).show();
            gpsupdates();
        }
        else if(timer == 1)
        {
            Toast.makeText(getApplicationContext(),"Started timer "+changedtime,Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt("timer",1);
            editor.commit();
            gpsupdates();
            chronometer.start();
            Log.e("time",""+chronometer.getBase());
            final Handler ha=new Handler();
            ha.postDelayed(new Runnable() {
                @Override
                public void run() {
                    chronometer.stop();
                    updatedtime = SystemClock.elapsedRealtime() - chronometer.getBase();
                    Toast.makeText(getApplicationContext(),"Stopped at"+updatedtime,Toast.LENGTH_LONG).show();
                    SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("Service change", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt("service", 0);
                    editor.commit();
                }
            }, timertime);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand()");
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "onTaskRemoved()");
        if(timer == 1)
        {
            changedtime = SystemClock.elapsedRealtime() - chronometer.getBase();
            timertime=timertime-changedtime;
            SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("Service change", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt("service",1);
            editor.putInt("timer",1);
            editor.putLong("timertime",timertime);
            editor.commit();
        }
        else if(timer == 0)
        {
            Toast.makeText(getApplicationContext(),"Without timer"+changedtime,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i(TAG, "onLowMemory()");
    }

    public void gpsupdates()
    {
        //Toast.makeText(getApplicationContext(),"GpsUpdates",Toast.LENGTH_LONG).show();
        final Handler ha=new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function

                SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("Service change", Context.MODE_PRIVATE);
                service=sharedpreferences.getInt("service",1);
                if(service == 1)
                {
                    Toast.makeText(getApplicationContext(),"Boot service running",Toast.LENGTH_LONG).show();
                    //                    gps.getLocation();
//                    if(gps.canGetLocation()){
//                        double latitude = gps.getLatitude();
//                        double longitude = gps.getLongitude();
//                        postGps(latitude,longitude);
////                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
////                            + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
//                    }else{
//                        gps.showSettingsAlert();
//                    }
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
        }, 5000);
    }

}

