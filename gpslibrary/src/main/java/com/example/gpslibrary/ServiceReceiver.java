package com.example.gpslibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class ServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED ) || intent.getAction().equalsIgnoreCase(Intent.ACTION_REBOOT )) {
            Intent serviceIntent = new Intent(context, StartService.class);
            context.startService(serviceIntent);
        }
        else if(intent.getAction().equals(Constants.START))
        {
            Intent serviceIntent = new Intent(context, StartService.class);
            SharedPreferences sharedpreferences = context.getApplicationContext().getSharedPreferences("Service change", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Constants.SERVICE_ON,Constants.TRUE);
            editor.putString(Constants.SERVICE_MODE,Constants.NORMAL_SERVICE);
            editor.commit();
            context.startService(serviceIntent);
            Toast.makeText(context,"started",Toast.LENGTH_LONG).show();

        }
        else if(intent.getAction().equals(Constants.STOP))
        {
            SharedPreferences sharedpreferences = context.getApplicationContext().getSharedPreferences("Service change", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Constants.SERVICE_MODE,Constants.NORMAL_SERVICE);
            editor.putString(Constants.SERVICE_ON,Constants.FALSE);
            editor.commit();
            Toast.makeText(context,"stopped",Toast.LENGTH_LONG).show();
        }
    }
}