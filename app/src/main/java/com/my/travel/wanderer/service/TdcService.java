package com.my.travel.wanderer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TdcService extends Service {
    public TdcService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
