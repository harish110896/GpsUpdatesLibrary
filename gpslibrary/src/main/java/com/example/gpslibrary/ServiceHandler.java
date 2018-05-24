package com.example.gpslibrary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.Toast;

public class ServiceHandler {


    public static void  startService(Context context){
        Intent i = new Intent(context.getApplicationContext(), StartService.class);
        SharedPreferences sharedpreferences = context.getApplicationContext().getSharedPreferences("Service change", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Constants.SERVICE_MODE,Constants.NORMAL_SERVICE);
        editor.putString(Constants.SERVICE_ON,Constants.TRUE);
        editor.commit();
        context.getApplicationContext().startService(i);
    }

    public static void stopService(Context context)
    {
        SharedPreferences sharedpreferences = context.getApplicationContext().getSharedPreferences("Service change", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Constants.SERVICE_MODE,Constants.NORMAL_SERVICE);
        editor.putString(Constants.SERVICE_ON,Constants.FALSE);
        editor.commit();
    }

    public static void TimedService(final Context context,String time)
    {

        int timerduration=Integer.parseInt(time);
        timerduration=timerduration*1000;
        Intent i = new Intent(context.getApplicationContext(), StartService.class);
        SharedPreferences sharedpreferences = context.getApplicationContext().getSharedPreferences("Service change", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Constants.SERVICE_MODE,Constants.TIMED_SERVICE);
        editor.putString(Constants.SERVICE_ON,Constants.TRUE);
        editor.putLong(Constants.TIMER_DURATION,timerduration);
        editor.commit();
        context.getApplicationContext().startService(i);
    }
    public static void setServiceStart(Context context,long time) {
        AlarmManager am = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context.getApplicationContext(), ServiceReceiver.class);
        i.setAction(Constants.START);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.set(AlarmManager.RTC_WAKEUP, time, pi);
        Toast.makeText(context.getApplicationContext(), "Start time is set for ", Toast.LENGTH_SHORT).show();
    }

    public static void setServiceStop(Context context,long time) {
        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent serviceintent = new Intent(context.getApplicationContext(), ServiceReceiver.class);
        serviceintent.setAction(Constants.STOP);
        PendingIntent pi = PendingIntent.getBroadcast(context.getApplicationContext(), 0, serviceintent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pi);
        Toast.makeText(context.getApplicationContext(), "End time is set", Toast.LENGTH_SHORT).show();
    }



}