package com.uis.conn.observer;

import android.app.Activity;

public interface MainAction {
    void actionA();
    void actionCacheA();
    void actionB();
    void actionCacheB();
    void actionC();
    void actionMultith();
    void attachActivity(Activity act);
}
