package com.example.gpslibrary;

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
        editor.putInt("service",1);
        editor.putInt("timer",0);
        editor.commit();
        context.getApplicationContext().startService(i);
    }

    public static void stopService(Context context)
    {
        Intent i = new Intent(context.getApplicationContext(), StartService.class);
        SharedPreferences sharedpreferences = context.getApplicationContext().getSharedPreferences("Service change", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("service", 0);
        editor.putInt("timer",0);
        editor.commit();
        context.getApplicationContext().startService(i);
    }

    public static void TimedService(final Context context,String time)
    {

        int timertime=Integer.parseInt(time);
        timertime=timertime*1000;
        Intent i = new Intent(context.getApplicationContext(), StartService.class);
        SharedPreferences sharedpreferences = context.getApplicationContext().getSharedPreferences("Service change", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("service",1);
        editor.putInt("timer",1);
        editor.putLong("timertime",timertime);
        editor.commit();
        context.getApplicationContext().startService(i);

    }



}