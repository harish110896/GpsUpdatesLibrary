package com.example.gpslibrary;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.service.voice.VoiceInteractionSession;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.Toast;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_STATE;


public class StartService extends Service {
    private static final String TAG = "Service";
    private Context mContext;
    GPSTracker gps;
    Chronometer chronometer;
    String Mode,ServiceOn;
    TelephonyManager telephonyManager;
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
        chronometer=new Chronometer(this);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if(checkPermission())
        {
            Constants.IMEI=telephonyManager.getDeviceId();
        }
        Constants.version=getPackageVersion(this);
        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("Service change", Context.MODE_PRIVATE);
        Mode=sharedpreferences.getString(Constants.SERVICE_MODE,Constants.NORMAL_SERVICE);
        Constants.TimerDuration=sharedpreferences.getLong(Constants.TIMER_DURATION,0);
        if(Mode.equals(Constants.NORMAL_SERVICE))
        {
            Toast.makeText(getApplicationContext(),"Started normal",Toast.LENGTH_LONG).show();
            StartGpsUpdates();
        }
        else if(Mode.equals(Constants.TIMED_SERVICE))
        {
            Toast.makeText(getApplicationContext(),"Started timer "+Constants.TimerStoppedAt,Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Constants.SERVICE_MODE,Constants.TIMED_SERVICE);
            editor.commit();
            StartGpsUpdates();
            chronometer.start();
            Log.e("time",""+chronometer.getBase());
            final Handler ha=new Handler();
            ha.postDelayed(new Runnable() {
                @Override
                public void run() {
                    chronometer.stop();
                    Constants.updatedtime = SystemClock.elapsedRealtime() - chronometer.getBase();
                    Toast.makeText(getApplicationContext(),"Stopped at"+Constants.updatedtime,Toast.LENGTH_LONG).show();
                    Constants.TimerStoppedAt=0;
                    SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("Service change", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(Constants.SERVICE_ON,Constants.FALSE);
                    editor.commit();
                }
            }, Constants.TimerDuration);
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
        if(Mode.equals(Constants.TIMED_SERVICE))
        {
            Constants.TimerStoppedAt = SystemClock.elapsedRealtime() - chronometer.getBase();
            Constants.TimerDuration=Constants.TimerDuration-Constants.TimerStoppedAt;
            SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("Service change", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Constants.SERVICE_MODE,Constants.TIMED_SERVICE);
            editor.putString(Constants.SERVICE_ON,Constants.TRUE);
            editor.putLong(Constants.TIMER_DURATION,Constants.TimerDuration);
            editor.commit();
        }
        else if(Mode.equals(Constants.NORMAL_SERVICE))
        {
            Toast.makeText(getApplicationContext(),"Without timer"+Constants.TimerStoppedAt,Toast.LENGTH_LONG).show();
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

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    public static int getPackageVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info.versionCode;
    }

    public void StartGpsUpdates()
    {
        final Handler ha=new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function
                SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("Service change", Context.MODE_PRIVATE);
                ServiceOn=sharedpreferences.getString(Constants.SERVICE_ON,Constants.TRUE);
                if(ServiceOn.equals(Constants.TRUE))
                {
                    Toast.makeText(getApplicationContext(),"Boot service running",Toast.LENGTH_LONG).show();
                    gps.getLocation();
                    if(gps.canGetLocation()){
                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();
                       // postGps(latitude,longitude,Constants.IMEI,Constants.version);
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                            + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    }else{
                        gps.showSettingsAlert();
                    }
                    ha.postDelayed(this, Constants.TimerInterval);

                }
                else if(ServiceOn.equals(Constants.FALSE))
                {
                    Toast.makeText(getApplicationContext(),"Boot service stopped",Toast.LENGTH_LONG).show();
                    gps.stopUsingGPS();
                    stopSelf();
                    ha.removeCallbacksAndMessages(this);
                }
            }
        }, 5000);
    }

//    public void postGps(double latitude,double longitude,String imei,int version)
//    {
//        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//        String url = "http://10.1.20.30:8080/location";
//        JSONObject postparams=new JSONObject();
//        try {
//            postparams.put("imei",imei);
//            postparams.put( "version",version);
//            postparams.put("latitude",latitude);
//            postparams.put( "longitude",longitude);
//        }
//        catch (JSONException js)
//        {
//
//        }
//        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
//                url, postparams,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                        //Success Callback
//
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
//
//                        //Failure Callback
//
//                    }
//                });
//        // Add the request to the RequestQueue.
//        queue.add(jsonObjReq);
//    }

}
