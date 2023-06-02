package com.uis.conn;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.MessageQueue;
import android.util.ArrayMap;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.tencent.mmkv.MMKV;
import com.uis.assignor.Assignor;
import com.uis.assignor.utils.ALog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Dapp extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        ALog.enableLog(Log.DEBUG);
        Assignor.init(this);
        Stack<String> st = new Stack<>();
        MMKV.initialize(this);
        DataProviderManager.INSTANCE.registerDataProvider("");
    }
}
