package com.uis.conn;

import android.app.Application;
import android.util.Log;
import com.uis.assignor.Assignor;
import com.uis.assignor.utils.ALog;

public class Dapp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ALog.enableLog(Log.DEBUG);
        Assignor.init(this);
    }
}
