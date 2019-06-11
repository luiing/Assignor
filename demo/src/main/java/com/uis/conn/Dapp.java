package com.uis.conn;

import android.app.Application;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.uis.assignor.utils.ALog;

public class Dapp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ALog.enableLog(Log.DEBUG);
        Fresco.initialize(this);
    }
}
