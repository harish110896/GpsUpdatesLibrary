package com.example.gpslibrary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class ServiceHandler {


    public static void  startService(Context context){
        Intent i = new Intent(context.getApplicationContext(), StartService.class);
        SharedPreferences sharedpreferences = context.getApplicationContext().getSharedPreferences("Service change", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("service",1);
        editor.commit();
        context.getApplicationContext().startService(i);
    }

    public static void stopService(Context context)
    {
        Intent i = new Intent(context.getApplicationContext(), StartService.class);
        SharedPreferences sharedpreferences = context.getApplicationContext().getSharedPreferences("Service change", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("service", 0);
        editor.commit();
        context.getApplicationContext().startService(i);
    }

}