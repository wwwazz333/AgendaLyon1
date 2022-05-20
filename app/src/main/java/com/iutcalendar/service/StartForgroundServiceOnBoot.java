package com.iutcalendar.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartForgroundServiceOnBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ForgroundServiceUpdate.start(context);
    }
}