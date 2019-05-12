package com.example.admin.techkid_session2.backgrounds;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class ScreenOnService extends Service {
    public ScreenOnService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //register receiver
        registerReceiver(new ScreenOnReceiver(), new IntentFilter(Intent.ACTION_SCREEN_ON));
        return START_STICKY;
    }
}
