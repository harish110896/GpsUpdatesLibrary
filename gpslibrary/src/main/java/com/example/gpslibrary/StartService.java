package com.example.gpslibrary;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.service.voice.VoiceInteractionSession;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class StartService extends Service {
    private static final String TAG = "Service";
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
        gps=new GPSTracker(this);
        //Toast.makeText(getApplicationContext(),"Started",Toast.LENGTH_LONG).show();

        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("Service change", Context.MODE_PRIVATE);
        int timer=sharedpreferences.getInt("timer",0);
        if(timer == 0)
        {
            Toast.makeText(getApplicationContext(),"Started normal",Toast.LENGTH_LONG).show();
            gpsupdates();
        }
        else if(timer == 1)
        {
            Toast.makeText(getApplicationContext(),"Started timer",Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt("timer",2);
            editor.commit();
            gpsupdates();
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
                int service=sharedpreferences.getInt("service",1);
                int timer=sharedpreferences.getInt("timer",0);
                int time=sharedpreferences.getInt("time",20000);
                if(service == 1)
                {
                    Toast.makeText(getApplicationContext(),"Boot service running",Toast.LENGTH_LONG).show();
                    gps.getLocation();
                    if(gps.canGetLocation()){
                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();
                        postGps(latitude,longitude);
//                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
//                            + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    }else{
                        gps.showSettingsAlert();
                    }
                    ha.postDelayed(this, 10000);

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

    public void postGps(double latitude,double longitude)
    {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://10.1.20.30:8080/location";
        JSONObject postparams=new JSONObject();
        try {
            postparams.put("latitude",latitude);
            postparams.put( "longitude",longitude);

        }
        catch (JSONException js)
        {

        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //Success Callback


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();

                        //Failure Callback

                    }
                });
        // Add the request to the RequestQueue.
        queue.add(jsonObjReq);
    }

}
