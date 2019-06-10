package com.uis.conn;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.WorkerThread;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.uis.connector.comm.Clog;
import com.uis.connector.comm.ConnPlant;

public class Dapp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Clog.enableLog();
        ConnPlant.setApp(this);
        Fresco.initialize(this);
    }
}
