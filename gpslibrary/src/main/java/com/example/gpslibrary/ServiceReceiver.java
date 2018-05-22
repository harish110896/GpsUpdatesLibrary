package com.example.gpslibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED ) || intent.getAction().equalsIgnoreCase(Intent.ACTION_REBOOT )) {
            Intent serviceIntent = new Intent(context, StartService.class);
            context.startService(serviceIntent);
        }
    }
}